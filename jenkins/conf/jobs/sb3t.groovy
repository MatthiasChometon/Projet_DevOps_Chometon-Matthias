#!groovy
println('------------------------------------------------------------------Import Job CI/SB3T')
def pipelineScript = new File('/var/jenkins_config/jobs/sb3t-pipeline.groovy').getText("UTF-8")

pipelineJob('CI/sb3t') {
    description("Build .jar from sb3t java application")
    parameters {
        stringParam {
            name('BRANCH')
            defaultValue('master')
            description("branch to push")
            trim(false)
        }
        booleanParam {
            name('SKIP_TEST')
            defaultValue(true)
            description("Skip test")
        }
        choice {
            name('VERSION_TYPE')
            choices(['SNAPSHOT', 'RELEASE'])
            description('Version type between snapshot and release')
        }
        stringParam {
            name('VERSION')
            defaultValue('1.0')
            description("version of project")
            trim(false)
        }
    }
    definition {
        cps {
            script(pipelineScript)
            sandbox()
        }
    }
}