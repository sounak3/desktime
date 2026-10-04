# Building DeskStop

This document describes how DeskStop is built, tested and packaged with Jenkins: the two pipelines, the build agents, what each agent needs installed, how the app stores its data, and how to back up and restore the build server.

## Pipelines at a glance

| Jenkins job | Pipeline file | Purpose | Trigger | Output |
|---|---|---|---|---|
| `desktime` | [`Jenkinsfile`](Jenkinsfile) | **Release.** Builds and tests the jar from source, then packages native installers on each OS. | Manual: *Build Now* | `DeskStop-<ver>.msi`, `deskstop_<ver>-release_amd64.deb`, `DeskStop-<ver>.dmg`, `desktime.jar` (archived in Jenkins) |
| `desktime dev` | [`Jenkinsfile.dev`](Jenkinsfile.dev) | **CI / testing.** Copies the jar your IDE just built onto all three test machines, then runs the unit tests. No installers. | Automatic, whenever `target/desktime.jar` changes on dell5558 | `desktime_latest.jar` on the Desktop of lin, mac and win (also archived in Jenkins) |

Typical workflow:

1. Build in the IDE (`mvn package`). This writes `target/desktime.jar`.
2. `desktime dev` runs automatically. It drops `desktime_latest.jar` on the Desktop of all three machines and runs the unit tests on a snapshot of your working copy.
3. Test the jar on Linux, macOS and Windows.
4. To release a new version, set `<version>` in `pom.xml` (for example `1.1-SNAPSHOT` → installers versioned `1.1`). Commit and push to `main`.
5. Run `desktime` (release). Download the installers from the build page and upload them to GitHub Releases.

Each build's description shows what it was built from:

- **Release:** `v1.0 @ 2834a2b5`, meaning the version and the commit it was built from.
- **Dev:** `2834a2b`, or `2834a2b + uncommitted changes` if the IDE build included work that wasn't committed yet.

### Version

`pom.xml` is the only place the version is set. The release pipeline reads `project.version`, removes `-SNAPSHOT`, and uses the result as the installer version. The MSI format accepts only numeric versions (`1.0`, `1.2.3`), and the pipeline stops with an error otherwise. The version also determines the installer file names, so the README download links must match it.

### Release pipeline flow

```
Build jar (lin)                         Package (parallel)
  checkout scm                     ┌──> Windows (win): jlink → jpackage --type msi (WiX 3.14)
  version from pom.xml             │
  mvn clean verify (tests) ─stash─>├──> Ubuntu  (lin): jlink → jpackage --type deb
  app/ + extras/ icons             └──> Mac OS  (mac): jlink → jpackage --type dmg
```

Only `lin` checks out the repository. `win` and `mac` receive the jar, default files and icons through `stash`/`unstash`, so they need neither git nor GitHub access. If a unit test fails, the release stops before packaging.

The `app/` folder is jpackage's `--input`, and everything in it ships inside the installer:

| File in installer | Source in repo |
|---|---|
| `desktime.jar` | `target/desktime.jar` (shaded jar with jlayer, built by Maven) |
| `DeskTime.xml` | `DeskTime.xml` (default settings for first start) |
| `Alarms.xml` | `Alarms.xml` (default alarms for first start) |
| `LICENSE.txt` | `LICENSE.md` (renamed, as jpackage's license file) |

Icons come from `extras/` (`DeskStop.ico` for Windows, `DeskStop.png` for Linux, `DeskStop.icns` for macOS) and are passed with `--icon`, so they are not copied into `app/`.

The Jenkinsfile is the only packaging definition. `pom.xml` used to have a second, conflicting jpackage setup, which has been removed.

## Where the app stores its data

Everything the app writes is per user, in `~/.deskstop/` (`%USERPROFILE%\.deskstop\` on Windows):

| File | Contents |
|---|---|
| `DeskTime.xml`, `DeskTime.xml.bak` | Clock panel settings, and the previous version of them |
| `Alarms.xml`, `Alarms.xml.bak` | Alarms, and the previous version of them |
| `resources/<jar build>/` | Bundled images and sounds, copied out of the jar on first start. The folder is replaced when the jar changes. |
| `DeskStop.lck` | Stops the same user from running two copies at once |

On start, settings are loaded from the first of these that can be read: `~/.deskstop/DeskTime.xml` → its `.bak` → `DeskTime.xml` next to the jar → `app/DeskTime.xml` next to the jar. If none exists, built-in defaults are used. Alarms work the same way. Saves always go to `~/.deskstop`. A save writes a temp file and then swaps it in, so a crash can't leave a truncated file. The default files next to the jar are optional, so running just `java -jar desktime.jar` from anywhere works.

When a stored image or sound path no longer exists (for example it was saved on another machine), the app uses the bundled file with the same name, or the default if that name isn't bundled.

## Parameters

| Job | Parameter | Default | Notes |
|---|---|---|---|
| `desktime dev` | `JAR_PATH` | `/home/sounak/Documents/NetBeansProjects/desktime/target/desktime.jar` | The IDE-built jar on the lin host. The working copy two folders above it is used for the commit info and the unit tests. |

The release job has no parameters. Its version comes from `pom.xml`.

## Build infrastructure

### Controller

| Item | Value |
|---|---|
| Runs as | Docker container `jenkins` on dell5558 |
| Image | `jenkins/jenkins:2.580.1-lts` (bundles Java 21) |
| URL (Jenkins root URL setting) | `http://192.168.1.14:8080/` |
| `JENKINS_HOME` | bind mount `/home/sounak/container/jenkins_home` → `/var/jenkins_home` |
| Ports | `8080` (web UI), `50000` (inbound agents) |
| JVM heap | `JAVA_OPTS="-Xms2g -Xmx4g"` |

The container cannot resolve mDNS `.local` names, so the agent host names are pinned with `--add-host`. Recreate the container with:

```bash
docker run -d --name jenkins \
  -p 8080:8080 -p 50000:50000 \
  -v /home/sounak/container/jenkins_home:/var/jenkins_home \
  --add-host dell5558.local:172.17.0.1 \
  --add-host macos.local:192.168.1.16 \
  --add-host winos.local:192.168.1.17 \
  -e JAVA_OPTS="-Xms2g -Xmx4g" \
  jenkins/jenkins:2.580.1-lts
```

If the LAN addresses change, update the `--add-host` entries, the **Jenkins URL** (*Manage Jenkins → System*) and the `-url` in each inbound agent's start command. A stale Jenkins URL makes every page take minutes to load.

### Agents (labels)

The pipelines select machines by label. Each node's name and label are the same.

| Label | Host | OS | Connection | Agent root | Used by |
|---|---|---|---|---|---|
| `lin` | `dell5558.local` (192.168.1.14, also runs the controller) | Ubuntu 24.04 | Inbound (JNLP). systemd **user** service `jenkins-agent-lin`, secret in `~/jenkins/agent-secret` (mode 600). Lingering is enabled, so it starts at boot. | `/home/sounak/jenkins` | release: Build jar, Ubuntu; dev: Collect jar, lin, Unit tests |
| `mac` | `macos.local` (192.168.1.16) | macOS | SSH from the controller, port 22, user `sounak`, credential *"for mac"* | `/Users/sounak/jenkins` | release: Mac OS; dev: mac |
| `win` | `winos.local` (192.168.1.17) | Windows 7 (VM) | Inbound (JNLP). Scheduled task `JenkinsWinAgent` (at logon) runs `c:\jenkins\run-agent.bat` in a retry loop. | `c:\jenkins` | release: Windows; dev: win |

### Rotating inbound agent secrets

An agent's secret is derived from its node name and a key in `secrets/jenkins.slaves.JnlpSlaveAgentProtocol.secret`. Deleting and recreating a node with the same name gives the **same** secret. To issue new secrets for all inbound agents:

1. Move `secrets/jenkins.slaves.JnlpSlaveAgentProtocol.secret` out of `JENKINS_HOME`. Keep it until the agents work again.
2. Restart Jenkins. A new key is generated.
3. Open *Manage Jenkins → Nodes → <node>* for each inbound node and copy the new secret into its start command:
   - `lin`: into `~/jenkins/agent-secret`
   - `win`: into `c:\jenkins\run-agent.bat`
4. Restart each agent.

The SSH-launched `mac` agent is not affected. Secrets were last rotated on 2026-10-04.

## Software required on each agent

All three agents need **JDK 21 or newer**. The Maven build targets Java 21 (`maven.compiler.release=21`, class file version 65), and the pipelines use `jlink --compress=zip-6`, which only exists from JDK 21 onwards. Jenkins 2.580 itself needs Java 17+ just to run the agent.

### lin (Ubuntu)

| Software | Why | Verified version |
|---|---|---|
| JDK 21 (`java`, `jdeps`, `jlink`, `jpackage` on `PATH`) | compile, minimal JRE, packaging | OpenJDK 21.0.12 |
| Maven (`mvn`) | build and tests in both pipelines | 3.8.7 |
| git | `checkout scm`; commit info in the dev pipeline | 2.43.0 |
| `dpkg-deb`, `fakeroot` | required by `jpackage --type deb` | dpkg 1.22.6, fakeroot 1.33 |
| `unzip`, `sha256sum`, `rsync` | jar check and working-copy snapshot in the dev pipeline | |
| `~/Desktop` | dev pipeline copies the jar there | |

Install everything with: `sudo apt install openjdk-21-jdk maven git fakeroot unzip rsync`

### win (Windows)

| Software | Why |
|---|---|
| JDK 21, with **`JAVA_HOME` set system-wide** | The Windows stages call `"%JAVA_HOME%\bin\jdeps"`, `jlink` and `jpackage` explicitly. Restart the agent after setting it, because it reads the environment at start-up. Alternatively, set `JAVA_HOME` on the node in Jenkins (*Nodes → win → Configure → Environment variables*). |
| `java` on `PATH` | Starts the agent (`run-agent.bat`); `java -version` in the dev pipeline |
| **.NET Framework 3.5.1** | Required by WiX Toolset 3.14, which jpackage uses to build the MSI. On Windows 7: *Control Panel → Programs → Turn Windows features on or off → Microsoft .NET Framework 3.5.1*. |
| Internet access to `github.com` | The release pipeline downloads `wix314-binaries.zip` on every run and adds it to `PATH`. Nothing to install manually. |
| `%USERPROFILE%\Desktop` | dev pipeline copies the jar there |

To check, run in a command prompt on the VM:

```cmd
echo %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version
"%JAVA_HOME%\bin\jpackage" --version
reg query "HKLM\SOFTWARE\Microsoft\NET Framework Setup\NDP\v3.5" /v Install
```

> **Windows 7 caveat:** JDK 21 does not officially support Windows 7. It often works, but nobody tests it there or fixes problems specific to it. The MSI it produces is fine: it contains the jar plus a Java 21 runtime, and installs and runs on Windows 10/11, where Java 21 is supported. Testing on Windows 7 tells you little about how the app behaves for Windows 10/11 users (high-DPI scaling, fonts, tray icon). Also, Windows 7 has had no security updates since 2020. Move `win` to a Windows 10/11 VM when you can.

### mac (macOS)

| Software | Why |
|---|---|
| JDK 21 (`java`, `jdeps`, `jlink`, `jpackage`) **on the non-interactive SSH `PATH`** | The SSH launcher runs commands in a non-login shell. Put `PATH` and `JAVA_HOME` in `~/.zshenv`, not `~/.zshrc`. The agent last reported Homebrew OpenJDK 21.0.4. |
| `hdiutil` | Builds the DMG. Built into macOS. |
| SSH enabled (*System Settings → General → Sharing → Remote Login*) | The controller connects to it |
| `~/Desktop` | dev pipeline copies the jar there |

To check from dell5558: `ssh -i ~/.ssh/id_ed25519_mac_admin sounak@macos.local 'command -v java jdeps jlink jpackage; java -version'`

The DMG's CPU architecture (Apple Silicon or Intel) is the architecture of the JDK installed on the Mac.

Checked on 2026-10-04: macOS 13.7.8 on Intel (x86_64), with Homebrew OpenJDK 21.0.4. The JDK is registered with `/usr/libexec/java_home`, so macOS's built-in `/usr/bin` launchers for `java`, `jdeps`, `jlink` and `jpackage` find it without any `PATH` setup. Releases therefore produce an **Intel-only DMG**, which Apple Silicon Macs run through Rosetta.

## Jenkins configuration

### Plugins the pipelines depend on

| Plugin | Used for |
|---|---|
| Pipeline (`workflow-aggregator`) and Pipeline: Declarative (`pipeline-model-definition`) | the `pipeline { }` syntax, `options`, `parameters`, `parallel` |
| Git (`git`) | *Pipeline script from SCM* and `checkout scm` |
| JUnit (`junit`) | test results on the build page; failed tests mark a dev build UNSTABLE |
| Workspace Cleanup (`ws-cleanup`) | `cleanWs()` |
| File Operations (`file-operations`) | `fileOperations`: download and unzip WiX on Windows |
| Pipeline: Basic Steps and Pipeline: Nodes and Processes | `stash`/`unstash`, `archiveArtifacts`, `timeout`, `sh`, `bat` |
| SSH Build Agents (`ssh-slaves`), SSH Credentials | launching the `mac` agent |

The old `desktime windows` job is disabled and was the only user of these plugins: `fstrigger`, `xtrigger-api`, `artifactdeployer`, `publish-over-ssh` and `publish-over`. They can be uninstalled once that job is deleted.

### Job setup

Both jobs read their pipeline from the repository:

*Configure → Pipeline → Definition: **Pipeline script from SCM*** → SCM: Git → Repository URL `https://github.com/sounak3/desktime.git`, Credentials: *none* (public repository) → Branch `*/main` → Script Path `Jenkinsfile` (release) or `Jenkinsfile.dev` (dev) → *Lightweight checkout* ✓.

The existing `jenkins` credential is an SSH key, which does nothing for `https://` URLs. If the repository ever becomes private, use a GitHub personal access token as a *Username with password* credential, or switch to the `git@github.com:sounak3/desktime.git` URL with the SSH key.

### Dev trigger (on dell5558)

A systemd path unit watches the jar and asks Jenkins to start `desktime dev`:

| File | Role |
|---|---|
| `~/.config/systemd/user/desktime-dev-trigger.path` | watches `target/desktime.jar` |
| `~/.config/systemd/user/desktime-dev-trigger.service` | runs `curl -X POST …/job/desktime%20dev/build`; skipped if the jar doesn't exist, for example right after `mvn clean` |
| `~/.config/desktime-dev-trigger.env` (mode 600) | `JENKINS_USER` and `JENKINS_TOKEN` (a Jenkins API token) |

To check that it works: `systemctl --user start desktime-dev-trigger.service`, then `journalctl --user -u desktime-dev-trigger.service -n 20`

A single Maven build writes the jar more than once (the shade plugin replaces it). The job's 15-second quiet period merges those triggers into one build. Each deploy stage times out after 5 minutes, so a powered-off VM only fails its own branch while the other machines still receive the jar.

## Backups

`JENKINS_HOME` holds things that exist nowhere else:

- credentials and the key that decrypts them (`secrets/`)
- the key that agent secrets are derived from
- node and job settings
- user accounts and API tokens
- build history and the archived installers of past releases

The data lives on disk `sda`, so backups go to a different physical disk, `sdb`.

| Item | Value |
|---|---|
| Schedule | systemd user timer `jenkins-backup.timer`, daily at 02:30 (± 10 min). Missed runs catch up after boot. |
| Script | `~/.local/bin/jenkins-backup.sh` |
| Destination | `/home/sounak/Library/backups/jenkins/jenkins_home-<date>-<time>.tar.gz`. The newest 7 are kept, readable only by you. |
| Excluded | `workspace/`, `caches/`, `war/` (all regenerated) and old plugin `.bak` files |

A backup contains decryptable credentials. Treat it like a password file, and don't copy it anywhere public.

**Restore:**

```bash
docker stop jenkins
mv /home/sounak/container/jenkins_home /home/sounak/container/jenkins_home.broken
tar -xzf /home/sounak/Library/backups/jenkins/jenkins_home-<date>-<time>.tar.gz -C /home/sounak/container
docker start jenkins     # or recreate it with the docker run command above
```

**Test a backup without touching the live server.** Unpack it into a scratch folder and start a second container with no network, so it can't contact your agents:

```bash
docker run -d --rm --name jenkins-restore-test --network none -v <scratch>/jenkins_home:/var/jenkins_home jenkins/jenkins:2.580.1-lts
docker logs -f jenkins-restore-test      # wait for "Jenkins is fully up and running", then: docker stop jenkins-restore-test
```

This test passed on 2026-10-04.

Run a backup now with `systemctl --user start jenkins-backup.service`. Check the timer with `systemctl --user list-timers jenkins-backup.timer`.

## Known issues and recommendations

1. **The committed root `desktime.jar` is unused.** It is 28 MB of Java 8 bytecode built in December 2024, older than the current source. No pipeline uses it. It is the reason clones were slow enough to need the old `git config pack.window/postBuffer` workarounds. Consider `git rm --cached desktime.jar` and adding it to `.gitignore`. The README's `java -jar desktime.jar` instructions refer to it, so update those too.
2. **The default `DeskTime.xml` contains developer paths.** It has 36 absolute paths such as `C:\Users\Sounak\Documents\NetBeansProjects\desktime\target\classes\sounds\…`. They're harmless now, because the app uses the bundled file of the same name, but the file should be regenerated from a clean first start.
3. **Publishing is manual.** Jenkins only archives the installers. Uploading them to GitHub Releases and updating the README checksums happen outside Jenkins.
4. **Installers are unsigned.** The MSI has no Authenticode signature (Windows SmartScreen will warn). The DMG is not notarized: Gatekeeper reports an unidentified developer, so open it with right-click → *Open*.
5. **Test machines need Java 21.** The jar from the dev pipeline needs a Java 21+ `java` on each test machine. Each deploy stage prints `java -version` so you can see this in the build log.
6. **Windows 7 is unsupported by Java 21.** See the caveat under *win*.
7. **What you test isn't exactly what you ship.** `desktime dev` tests your IDE build, which may include uncommitted changes. The release rebuilds from `main`. The build descriptions record the commit, so you can at least see whether they match.
8. **Settings use `XMLDecoder`.** It can create any Java object named in the file. The file lives in the user's own home folder, so the risk is low, but a plain format (JSON or properties) would remove it.
