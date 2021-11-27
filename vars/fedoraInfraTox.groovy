#!groovy

def call(ArrayList distros = ["f30", "f31", "f32", "f33", "f34", "f35", "latest"], Closure body) {
    def stages = [:]
    def fedora_containers = [
                    containerTemplate(name: 'jnlp',
                                      image: "image-registry.openshift-image-registry.svc:5000/openshift/cico-workspace:latest",
                                      ttyEnabled: false,
                                      args: '${computer.jnlpmac} ${computer.name}',
                                      workingDir: "/workdir")
    ]

    distros.each { fedora ->
        stages["tox-${fedora}"] = {
            stage("tox-${fedora}"){
                container("${fedora}"){
                    withEnv(['HOME=/workdir/workspace']){
                        sh "cp -al ./ ../${fedora}/"
                        dir( "../${fedora}" ){
                            sh "rm -rf .tox"
                            sh "tox"
                        }
                    }
                }
            }
        }

        fedora_containers.add(containerTemplate(name: "${fedora}",
                                                image: "quay.io/centosci/python-tox:${fedora}",
                                                ttyEnabled: true,
                                                alwaysPullImage: true,
                                                command: "cat",
                                                workingDir: '/workdir'))
    }

    podTemplate(name: 'fedora-tox',
                label: 'fedora-tox',
                cloud: 'openshift',
                containers: fedora_containers
    ){
        node('fedora-tox'){
            ansiColor('xterm'){
                stage ('checkout'){
                    checkout scm
                }

                parallel stages
            }
        }
    }
}
