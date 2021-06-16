## Backup Details
The backup job "jobs/pipelines/daily-jenkins-backup-job/Jenkinsfile" is responsible for creating backup of influx-db and jenkins.
It stores maximum 5 backup files in cos storage. 

The buckets are present in [ocp4-on-power](https://cloud.ibm.com/objectstorage/crn%3Av1%3Abluemix%3Apublic%3Acloud-object-storage%3Aglobal%3Aa%2F65b64c1f1c29460e8c2e4bbfbd893c2c%3A8aeefa98-c07b-4d22-aa7a-1694374ae275%3A%3A?paneId=manage)

| Bucket Name | Region | Resource Group |
| :---: | :---: | :---: |
| influxdb-backup-storage | us-south | ocp-cicd-resource-group |
| jenkins-backup-storage | us-south | ocp-cicd-resource-group |