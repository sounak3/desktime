# Building DeskStop

This document describes how DeskStop is built, tested and packaged with Jenkins: the two pipelines, the build agents, what each agent needs installed, and how to rebuild the setup.

## Pipelines at a glance

| Jenkins job | Pipeline file | Purpose | Trigger | Output |
|---|---|---|---|---|
| `desktime` | [`Jenkinsfile`](Jenkinsfile) | **Release.** Builds the jar from source, then packages native installers on each OS. | Manual: *Build with Parameters* | `DeskStop-<ver>.msi`, `deskstop_<ver>-release_amd64.deb`, `DeskStop-<ver>.dmg`, `desktime.jar` (archived in Jenkins) |
| `desktime dev` | [`Jenkinsfile.dev`](Jenkinsfile.dev) | **CI / testing.** Copies the jar your IDE just built onto all three test machines. No installers. | Automatic, whenever `target/desktime.jar` changes on dell5558 | `desktime_latest.jar` on the Desktop of lin, mac and win (also archived in Jenkins) |

Typical workflow:

1. Build in the IDE (`mvn package`). This writes `target/desktime.jar`.
2. `desktime dev` runs automatically and drops `desktime_latest.jar` on the Desktop of all three machines.
3. Test the jar on Linux, macOS and Windows.
4. Commit and push to `main`.
5. Run `desktime` (release) with the version you want. Download the installers from the build page and upload them to GitHub Releases.

### Release pipeline flow

```
Build jar (lin)                        Package (parallel)
  checkout scm                    ┌──> Windows (win): jlink → jpackage --type msi (WiX 3.14)
  mvn clean package    ──stash──> ├──> Ubuntu  (lin): jlink → jpackage --type deb
  app/ + extras/ icons            └──> Mac OS  (mac): jlink → jpackage --type dmg
```

Only `lin` checks out the repository. `win` and `mac` receive the jar, settings files and icons through `stash`/`unstash`, so they need neither git nor GitHub access.

The `app/` folder is jpackage's `--input`. Everything in it ships inside the installer:

| File in installer | Source in repo |
|---|---|
| `desktime.jar` | `target/desktime.jar` (shaded jar with jlayer, built by Maven) |
| `DeskTime.xml` | `DeskTime.xml` (default settings) |
| `Alarms.xml` | `Alarms.xml` (default alarms) |
| `LICENSE.txt` | `LICENSE.md` (renamed, as jpackage's license file) |

Icons come from `extras/` (`DeskStop.ico` for Windows, `DeskStop.png` for Linux, `DeskStop.icns` for macOS) and are passed with `--icon`, so they are not copied into `app/`.

## Parameters

| Job | Parameter | Default | Notes |
|---|---|---|---|
| `desktime` | `APP_VERSION` | `1.0` | Installer version. Must be numeric (`1.0`, `1.2.3`): the MSI format rejects anything else, such as `1.0-SNAPSHOT`. It also determines the file names, so the README download links must match. |
| `desktime dev` | `JAR_PATH` | `/home/sounak/Documents/NetBeansProjects/desktime/target/desktime.jar` | Path of the IDE-built jar on the lin host. |

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
| `lin` | `dell5558.local` (192.168.1.14, also runs the controller) | Ubuntu 24.04 | Inbound (JNLP). systemd **user** service `jenkins-agent-lin` (`~/.config/systemd/user/jenkins-agent-lin.service`), lingering enabled so it starts at boot | `/home/sounak/jenkins` | release: Build jar, Ubuntu; dev: Collect jar, lin |
| `mac` | `macos.local` (192.168.1.16) | macOS | SSH from the controller, port 22, user `sounak`, credential *"for mac"* | `/Users/sounak/jenkins` | release: Mac OS; dev: mac |
| `win` | `winos.local` (192.168.1.17) | Windows 7 (VM) | Inbound (JNLP). Scheduled task `JenkinsWinAgent` (at logon) runs `c:\jenkins\run-agent.bat` in a retry loop | `c:\jenkins` | release: Windows; dev: win |

The inbound agent secrets are stored in plain text in the lin systemd unit and in `c:\jenkins\run-agent.bat`. To rotate a secret, delete and recreate the node in Jenkins, then update the file.

## Software required on each agent

All three agents need **JDK 21 or newer**. The Maven build targets Java 21 (`maven.compiler.release=21`, class file version 65). The pipelines also use `jlink --compress=zip-6`, which only exists from JDK 21 onwards. Jenkins 2.580 itself needs Java 17+ just to run the agent.

### lin (Ubuntu)

| Software | Why | Verified version |
|---|---|---|
| JDK 21 (`java`, `jdeps`, `jlink`, `jpackage` on `PATH`) | compile, minimal JRE, packaging | OpenJDK 21.0.12 |
| Maven (`mvn`) | `mvn clean package` in the release pipeline | 3.8.7 |
| git | `checkout scm` (only lin checks out code) | 2.43.0 |
| `dpkg-deb`, `fakeroot` | required by `jpackage --type deb` | dpkg 1.22.6, fakeroot 1.33 |
| `unzip`, `sha256sum` | jar sanity check in the dev pipeline | coreutils / unzip |
| `~/Desktop` | dev pipeline copies the jar there | |

Install everything with: `sudo apt install openjdk-21-jdk maven git fakeroot unzip`

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

> **Windows 7 caveat:** JDK 21 does not officially support Windows 7 (vendors support Windows 10 and later), and neither does the Java 21 runtime that jpackage bundles into the MSI. It may work, but it is unsupported. Consider moving `win` to a Windows 10/11 VM.

### mac (macOS)

| Software | Why |
|---|---|
| JDK 21 (`java`, `jdeps`, `jlink`, `jpackage`) **on the non-interactive SSH `PATH`** | The SSH launcher runs commands in a non-login shell. Put `PATH` and `JAVA_HOME` in `~/.zshenv`, not `~/.zshrc`. The agent last reported Homebrew OpenJDK 21.0.4. |
| `hdiutil` | Builds the DMG. Built into macOS. |
| SSH enabled (*System Settings → General → Sharing → Remote Login*) | The controller connects to it |
| `~/Desktop` | dev pipeline copies the jar there |

To check from another machine: `ssh sounak@macos.local 'command -v java jdeps jlink jpackage; java -version'`

The DMG's CPU architecture (Apple Silicon or Intel) is the architecture of the JDK installed on the Mac.

## Jenkins configuration

### Plugins the pipelines depend on

| Plugin | Used for |
|---|---|
| Pipeline (`workflow-aggregator`) and Pipeline: Declarative (`pipeline-model-definition`) | the `pipeline { }` syntax, `parameters`, `options`, `parallel` |
| Git (`git`) | *Pipeline script from SCM* and `checkout scm` |
| Workspace Cleanup (`ws-cleanup`) | `cleanWs()` |
| File Operations (`file-operations`) | `fileOperations`: download and unzip WiX on Windows |
| Pipeline: Basic Steps and Pipeline: Nodes and Processes | `stash`/`unstash`, `archiveArtifacts`, `timeout`, `sh`, `bat` |
| SSH Build Agents (`ssh-slaves`), SSH Credentials | launching the `mac` agent |

The old `desktime windows` job was the only user of these plugins: `fstrigger`, `xtrigger-api`, `artifactdeployer`, `publish-over-ssh` and `publish-over`. They can be uninstalled once that job is deleted.

### Job setup

Both jobs read their pipeline from the repository:

*Configure → Pipeline → Definition: **Pipeline script from SCM*** → SCM: Git → Repository URL `https://github.com/sounak3/desktime.git`, Credentials: *none* (public repository) → Branch `*/main` → Script Path `Jenkinsfile` (release) or `Jenkinsfile.dev` (dev) → *Lightweight checkout* ✓.

- `desktime dev` is already configured this way. The pipeline file must exist on `main` on GitHub before the job can run.
- `desktime` still contains an older inline copy of the pipeline. Switch it to *Pipeline script from SCM* after this repository's `Jenkinsfile` is pushed.
- The existing `jenkins` credential is an SSH key. It does nothing for `https://` URLs. If the repository ever becomes private, use a GitHub personal access token as a *Username with password* credential, or switch to the `git@github.com:sounak3/desktime.git` URL with the SSH key.

### Dev trigger (on dell5558)

A systemd path unit watches the jar and asks Jenkins to start `desktime dev`:

| File | Role |
|---|---|
| `~/.config/systemd/user/desktime-dev-trigger.path` | watches `target/desktime.jar` |
| `~/.config/systemd/user/desktime-dev-trigger.service` | runs `curl -X POST …/job/desktime%20dev/build`; skipped if the jar doesn't exist, for example right after `mvn clean` |
| `~/.config/desktime-dev-trigger.env` (mode 600) | `JENKINS_USER` and `JENKINS_TOKEN` |

One-time setup:

1. In Jenkins: *your user → Security → API Token → Add new Token*. Copy the token.
2. Put your user name and token in `~/.config/desktime-dev-trigger.env`.
3. Enable the watcher: `systemctl --user enable --now desktime-dev-trigger.path`
4. Test without rebuilding: `systemctl --user start desktime-dev-trigger.service`, then `journalctl --user -u desktime-dev-trigger.service -n 20`

A single Maven build writes the jar more than once (the shade plugin replaces it). The job's 15-second quiet period merges those triggers into one build. Each deploy stage times out after 5 minutes, so a powered-off VM only fails its own branch while the other machines still receive the jar.

## Known issues and recommendations

1. **The default settings file ships developer paths.** `DeskTime.xml` contains 36 absolute paths such as `C:\Users\Sounak\Documents\NetBeansProjects\desktime\target\classes\sounds\beep-warning-6387.mp3`. It is copied into every installer as the default settings, so on users' machines (and on any Linux or Mac) those sound and image paths don't exist. It should be regenerated with paths that work on any machine, such as references to resources inside the jar.
2. **The committed root `desktime.jar` is now unused.** It is 28 MB of Java 8 bytecode built in December 2024, older than the current source. No pipeline uses it any more. It is the reason clones were slow enough to need the `git config pack.window/postBuffer` workarounds. Consider `git rm --cached desktime.jar` and adding it to `.gitignore`. The README's `java -jar desktime.jar` instructions refer to it, so update those too.
3. **No automated tests.** There is no `src/test`, so `mvn package` only compiles. The dev pipeline exists to make manual testing on all three OSes easy.
4. **Versions are maintained by hand.** `pom.xml` says `1.0-SNAPSHOT`, the installer version is the `APP_VERSION` parameter, and the copyright year (`2024`) is hard-coded in the `jpackage` arguments. The README download links (`DeskStop-1.0.msi` and so on) contain the version and the MD5 checksums. They must be updated by hand on each release.
5. **Publishing is manual.** Jenkins only archives the installers. Uploading them to GitHub Releases and updating the README checksums happen outside Jenkins.
6. **Installers are unsigned.** The MSI has no Authenticode signature (Windows SmartScreen will warn). The DMG is not notarized: Gatekeeper reports an unidentified developer, so open it with right-click → *Open*.
7. **Test machines need Java 21.** The jar from the dev pipeline needs a Java 21+ `java` on each test machine. Each deploy stage prints `java -version` so you can see this in the build log.
8. **Windows 7 is unsupported by Java 21.** See the caveat under *win*.
9. **Back up `JENKINS_HOME`.** A one-off backup exists at `/home/sounak/container/backups/`. Nothing makes backups automatically. A scheduled `tar` (cron) of `/home/sounak/container/jenkins_home`, excluding `workspace/`, or the ThinBackup plugin, would protect the job, node and credential configuration.
