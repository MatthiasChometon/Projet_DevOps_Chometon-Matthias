# Projet_DevOps_Chometon-Matthias

Objectif du devoir maison :

Le but de ce projet est de mettre en pratique l’ensemble des connaissances acquises
pendant les 4 journées de formation, afin de réaliser une infrastructure de staging pour une
application java (fournie).

  ## Jenkins
  
Objectif :

Une pipeline jenkins permettant de :
- Créer un jar à partir du repository Git: https://github.com/Ozz007/sb3t
- Lancer les tests unitaires et tests d’intégrations
- Déposer le jar dans le workspace jenkins

Explication :

Dans le fichier sb3t.groovy, je déclare mes variables. Je pourrais les initialisées sur jenkins pour changer ma pipeline en fonction de ses paramètres. Dans ma pipeline (fichier sb3t-pipeline.groovy), je découpe ma pipeline en plusieurs étapes (des stage). Grace à jenkins je peux faire des conditions qui me permette par exemple de lancer mes tests uniquement si params.SKIP_TESTS est à false.
Exemple pour les tests unitaires :

```
stage('testunit') {
    when {
    expression { params.SKIP_TESTS == false }
    }
    steps {
        sh 'mvn test'
    }
}
```