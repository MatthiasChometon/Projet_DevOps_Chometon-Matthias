#!groovy
println('------------------------------------------------------------------Import Job IaC/terraform')
def pipelineScript = new File('/var/jenkins_config/jobs/terraform-pipeline.groovy').getText("UTF-8")

pipelineJob('IaC/terraform') {
    description("Create instance aws terraform")
    definition {
        cps {
            script(pipelineScript)
            sandbox()
        }
    }
}