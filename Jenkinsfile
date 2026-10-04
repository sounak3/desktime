pipeline {
    agent none
    parameters {
        string(name: 'APP_VERSION', defaultValue: '1.0', description: 'Installer version (numeric, e.g. 1.0 or 1.2.3). MSI requires this format.')
    }
    stages {
        stage('Build jar') {
            agent { label 'lin' }
            steps {
                cleanWs()
                checkout scm
                sh 'mvn -B -ntp clean package'
                sh '''
                    mkdir -p app
                    cp target/desktime.jar DeskTime.xml Alarms.xml app/
                    cp LICENSE.md app/LICENSE.txt
                '''
                stash name: 'app', includes: 'app/**'
                stash name: 'icons', includes: 'extras/DeskStop.*'
                archiveArtifacts artifacts: 'app/desktime.jar', fingerprint: true
            }
        }
        stage('Package') {
            parallel {
                stage('Windows') {
                    agent { label 'win' }
                    stages {
                        stage('Windows: Creating minimal JRE') {
                            steps {
                                cleanWs()
                                unstash 'app'
                                unstash 'icons'
                                script {
                                    def deps = bat(returnStdout: true, script: '@echo off && "%JAVA_HOME%"\\bin\\jdeps --print-module-deps app\\desktime.jar').trim()
                                    echo "Dependencies: '${deps}'"
                                    withEnv(["DEPENDS=${deps}"]) {
                                        bat '@echo off && "%JAVA_HOME%"\\bin\\jlink --compress=zip-6 --strip-debug --no-header-files --no-man-pages --add-modules "%DEPENDS%" --output jre'
                                    }
                                }
                            }
                        }
                        stage('Pack Windows Installer') {
                            steps {
                                script {
                                    fileOperations([
                                        fileDownloadOperation(password: '', proxyHost: '', proxyPort: '', targetFileName: 'wix314-binaries.zip', targetLocation: '', url: 'https://github.com/wixtoolset/wix3/releases/download/wix3141rtm/wix314-binaries.zip', userName: ''),
                                        fileUnZipOperation(filePath: 'wix314-binaries.zip', targetLocation: 'wix')
                                    ])
                                    def wix = pwd() + '\\wix'
                                    withEnv(["PATH+WIX=${wix}"]) {
                                        bat '@echo off && "%JAVA_HOME%"\\bin\\jpackage --input app --name DeskStop --description "Clock, Uptime and Pomodoro application" --vendor "Sounak Choudhury" --copyright "Copyright (C) 2024 Sounak Choudhury" --app-version %APP_VERSION% --main-jar desktime.jar --runtime-image jre --type msi --license-file app\\LICENSE.txt --icon extras\\DeskStop.ico --win-dir-chooser --win-menu --win-menu-group DeskStop --win-shortcut'
                                    }
                                }
                            }
                        }
                        stage('Export MSI') {
                            steps {
                                archiveArtifacts artifacts: '*.msi', followSymlinks: false
                            }
                        }
                    }
                }
                stage('Ubuntu') {
                    agent { label 'lin' }
                    stages {
                        stage('Ubuntu: Creating minimal JRE') {
                            steps {
                                cleanWs()
                                unstash 'app'
                                unstash 'icons'
                                sh 'jlink --compress=zip-6 --strip-debug --no-header-files --no-man-pages --add-modules "$(jdeps --print-module-deps app/desktime.jar)" --output jre'
                            }
                        }
                        stage('Pack Debian Package') {
                            steps {
                                sh 'jpackage --input app --name DeskStop --description "Clock, Uptime and Pomodoro application" --vendor "Sounak Choudhury" --copyright "Copyright (C) 2024 Sounak Choudhury" --app-version "$APP_VERSION" --main-jar desktime.jar --runtime-image jre --type deb --license-file app/LICENSE.txt --icon extras/DeskStop.png --linux-app-category Utility --linux-app-release release --linux-menu-group Office --linux-shortcut'
                            }
                        }
                        stage('Export DEB') {
                            steps {
                                archiveArtifacts artifacts: '*.deb', followSymlinks: false
                            }
                        }
                    }
                }
                stage('Mac OS') {
                    agent { label 'mac' }
                    stages {
                        stage('MacOS: Creating minimal JRE') {
                            steps {
                                cleanWs()
                                unstash 'app'
                                unstash 'icons'
                                sh 'jlink --compress=zip-6 --strip-debug --no-header-files --no-man-pages --add-modules "$(jdeps --print-module-deps app/desktime.jar)" --output jre'
                            }
                        }
                        stage('Pack DMG Image') {
                            steps {
                                sh 'jpackage --input app --name DeskStop --description "Clock, Uptime and Pomodoro application" --vendor "Sounak Choudhury" --copyright "Copyright (C) 2024 Sounak Choudhury" --app-version "$APP_VERSION" --main-jar desktime.jar --runtime-image jre --type dmg --license-file app/LICENSE.txt --icon extras/DeskStop.icns --mac-app-category utilities --mac-package-name DeskStop'
                            }
                        }
                        stage('Export DMG Image') {
                            steps {
                                archiveArtifacts artifacts: '*.dmg', followSymlinks: false
                            }
                        }
                    }
                }
            }
        }
    }
}
