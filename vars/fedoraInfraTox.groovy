#!groovy

def call(Closure body) {
    def stages = [:]
    def fedora_containers = [
                    containerTemplate(name: 'jnlp',
                                      image: "docker-registry.default.svc:5000/openshift/cico-workspace:latest",
                                      ttyEnabled: false,
                                      args: '${computer.jnlpmac} ${computer.name}',
                                      workingDir: "/workdir")
    ]
    def active_fedoras = ["f30", "f31", "rawhide"]

    active_fedoras.each { fedora ->
        stages["tox-${fedora}"] = {
            stage("tox-${fedora}"){
                container("${fedora}"){
                    sh "cp -al ./ ../${fedora}/"
                    dir( "../${fedora}" ){
                        sh "rm -rf .tox"
                        sh "tox"
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
