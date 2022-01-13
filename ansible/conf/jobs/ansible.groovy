#!groovy
println('------------------------------------------------------------------Import Job CAC/ANSIBLE')
def pipelineScript = new File('/var/jenkins_config/jobs/ansible-pipeline.groovy').getText("UTF-8")

pipelineJob('CAC/ansible') {
    description("ANSIBLE")
    definition {
        cps {
            script(pipelineScript)
            sandbox()
        }
    }
}