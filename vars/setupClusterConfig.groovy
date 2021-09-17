def call(String config, String noOfWorkers="2"){
    script{
        //Setup PowerVS cluster config
        //Min config
        if (config == "min") {
            env.BASTION_MEMORY = "8"
            env.BASTION_PROCESSORS = ".5"
            env.BASTION_VCPUS = "2"

            env.BOOTSTRAP_MEMORY = "16"
            env.BOOTSTRAP_PROCESSORS = ".5"
            env.BOOTSTRAP_VCPUS = "2"

            env.NUM_OF_MASTERS = "3"
            env.MASTER_PROCESSORS = ".5"
            env.MASTER_VCPUS = "1" 

            env.NUM_OF_WORKERS = noOfWorkers
            env.WORKER_PROCESSORS = ".5"
            env.WORKER_VCPUS = "1" 

            if (OCP_RELEASE == "4.6" || OCP_RELEASE == "4.7") {
                env.MASTER_MEMORY = "32"
                env.WORKER_MEMORY = "32"
            }
            else {
                env.MASTER_MEMORY = "16"
                env.WORKER_MEMORY = "16"
            }
        }
        //Max Config
        else{
            env.BASTION_MEMORY = "8"
            env.BASTION_PROCESSORS = ".5"
            env.BASTION_VCPUS = "2"

            env.BOOTSTRAP_MEMORY = "16"
            env.BOOTSTRAP_PROCESSORS = ".5"
            env.BOOTSTRAP_VCPUS = "2"

            env.NUM_OF_MASTERS = "3"
            env.MASTER_PROCESSORS = ".5"
            env.MASTER_VCPUS = "2"
            env.MASTER_MEMORY = "64"

            env.NUM_OF_WORKERS = noOfWorkers
            env.WORKER_PROCESSORS = ".5"
            env.WORKER_VCPUS = "3" 
            env.WORKER_MEMORY = "64"
        }
    }
}
