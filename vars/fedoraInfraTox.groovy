#!groovy

def call(Closure body) {
    def stages = [:]
    def fedora_containers = [
                    containerTemplate(name: 'jnlp',
                                      image: "docker-registry.default.svc:5000/bstinson/agent-python3-tox:latest",
                                      ttyEnabled: false,
                                      args: '${computer.jnlpmac} ${computer.name}',
                                      workingDir: "/workdir")
    ]
    def active_fedoras = ["f28", "f29", "rawhide"]

    active_fedoras.each { fedora ->
        stages["tox-${fedora}"] = {
            stage("tox-${fedora}"){
                container("${fedora}"){
                    sh "cp -al ../${env.JOB_NAME} ../${env.JOB_NAME}-${fedora}/"
                    dir( "../${env.JOB_NAME}-${fedora}" ){
                        sh "rm -rf .tox"
                        sh "tox"
                    }
                }
            }
        }

        fedora_containers.add(containerTemplate(name: "${fedora}",
                                                image: "docker-registry.default.svc:5000/bstinson/python-tox:${fedora}",
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
        node('mypod'){
            ansiColor('xterm'){
                stage ('checkout'){
                    checkout scm
                }

                parallel stages
            }
        }
    }
}
