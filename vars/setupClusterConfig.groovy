def call(String config, String noOfWorkers="2"){
    script{
        //Setup PowerVS cluster config
        env.BASTION_MEMORY = "8"
        env.BASTION_PROCESSORS = ".5"

        env.BOOTSTRAP_MEMORY = "16"
        env.BOOTSTRAP_PROCESSORS = ".5"

        env.NUM_OF_MASTERS = "3"
        env.MASTER_PROCESSORS = ".5"

        env.NUM_OF_WORKERS = noOfWorkers
        env.WORKER_PROCESSORS = ".5"
        //Min config
        if (config == "min") {
            if (OCP_RELEASE == "4.5") {
                env.MASTER_MEMORY = "16"
                env.WORKER_MEMORY = "16"
            }
            else {
                env.MASTER_MEMORY = "32"
                env.WORKER_MEMORY = "32"
            }
        }
        //Max Config
        else{
            if (OCP_RELEASE == "4.5") {
                env.MASTER_MEMORY = "32"
                env.WORKER_MEMORY = "32"
            }
            else {
                env.MASTER_MEMORY = "64"
                env.WORKER_MEMORY = "64"
            }
        }
    }
}
