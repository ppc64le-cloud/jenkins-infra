@NonCPS
 def call(release){
     def command = """ wget  --output-document  -  https://mirror.openshift.com/pub/openshift-v4/clients/ocp-dev-preview/latest-$release/"""
     build = command.execute().text
     def min1 = ~/<a href="/
     def min3 = ~/">.*/
     def min4 = ~/\[/
     def ls =''
     build.eachLine{
         if (it =~ /^<tr/) {
             String lst = it.findAll(/<a href=".*/)
             if (lst.contains("client")) {
                 if (lst.contains("linux")) {
                     if (lst.contains("nightly")) {
                         ls = ls + " " + (lst - min1)
                         ls = ls - min3
                         ls = ls - min4
                     }
                 }
             }
         }
     }
     //bld = ls.split() as List
     def lsnew = ls.trim()
     return "https://mirror.openshift.com/pub/openshift-v4/clients/ocp-dev-preview/latest-$release/$lsnew"
 }