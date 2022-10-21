## Backup Details
The backup job "jobs/pipelines/daily-jenkins-backup-job/Jenkinsfile" is responsible for creating backup of influx-db and jenkins.
It stores maximum 5 backup files in cos storage. 

The buckets are present in [ibm-internal-cicd-cos](https://cloud.ibm.com/objectstorage/crn%3Av1%3Abluemix%3Apublic%3Acloud-object-storage%3Aglobal%3Aa%2F7cfbd5381a434af7a09289e795840d4e%3A28b5b206-4182-4324-8f5d-0d9234989a92%3A%3A?paneId=manage)

| Bucket Name | Region | Resource Group |
| :---: | :---: | :---: |
| ibm-internal-cicd-infuxdb-backup-bucket | us-south | ibm-internal-cicd-resource-group |
| ibm-internal-cicd-jenkins-backup-bucket | us-south | ibm-internal-cicd-resource-group |