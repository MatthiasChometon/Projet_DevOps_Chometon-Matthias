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

  ## Terraform
  
Objectif :

Une pipeline jenkins permettant de générer une instance aws créée et explicitée via
Terraform.
Le code Terraform devra donc permettre de déclarer :
- Une instance aws.
- Une clef ssh qui sera automatiquement rapatriée dans l’instance.
- (Optionnel) Un disque de data supplémentaire.
- Un security group permettant l’ouverture au protocol ssh et aux ports 22/8080 de
notre application en entrée et vers tout le monde en sortie.
- Cloud init pour la création d’un user deploy et l’installation du paquet python si
nécessaire

Explication :

Dans le dossier ssh je crée mon couple de clefs ssh. Dans mon fichier 'main.tf', je déclare les paramètres d'aws :

```
provider "aws" {
  profile                 = "default"
  region                  = "us-east-2"
  shared_credentials_file = "credential.txt"
}
```

Je définis mon instance d'aws :

```
resource "aws_instance" "app_server" {
  ami           = "ami-0d97ef13c06b05a19"
  instance_type = "t2.micro"
  user_data = data.template_file.user_data.rendered
  key_name = "deployer-keys-chometon"
  vpc_security_group_ids = [ aws_security_group.sg_default.id ]
  associate_public_ip_address = true

  tags = {
    Name   = "chometon-app"
    groups = "app"
    owner  = "matthias-chometon"
  }
}
```

Je déclare ma paire de clef ssh :

```
resource "aws_key_pair" "deployer" {
  key_name = "deployer-keys-chometon"
  public_key = file("./ssh/id_rsa.pub")
}

```

Je déclare mon groupe de sécurité (aws_security_group). J'expose mes ressources grace à mes output.

Ensuite je crée mon fichier credential.txt dans le dossier terraform-files.

Dans la partie jenkins, je crée une pipeline pour créer une instance aws (terraform.groovy), une pipeline initialiser terraform et une pipeline pour détruire mon instance terraform.

  ## Ansible

Objectif :

 Une pipeline jenkins permettant la configuration de l’instance via ansible.
Le code ansible devra donc permettre via l’utilisateur deploy créé dans la partie IaC :
- D’installer java 11
- De créer un user java_user pour le lancement de l’application java (jar)
- D’installer le jar
https://github.com/Ozz007/sb3t/releases/download/1.0/sb3t-ws-1.0.jar sur le serveur
- De lancer l'application java (jar) avec la configuration suivante:
- Taille de la jvm 128MB
- De s’assurer que le port d’écoute de l’application est bien en écoute par notre
application et accessible depuis l’ip publique de notre VM
- L’API health de l’application doit être accessible : http://IP_PUBLIQUE_AWS:8080/api/actuator/health

Explication :

On initialize notre galaxy grace à la commande : ansible-galaxy init JavaCac. Dans notre dossier vars on ajouter nos variables contenant le path du jar, le nom du jar, la taille de la jvm... Dans notre dossier tasks, on déclare nos tasks permettant d'installer java,

```
- name: Install java
  become: yes
  yum:
    name: java-11-openjdk
```

créer un utilisateur pour le lancement application,

```
- name: Create User java
  become: yes
  ansible.builtin.user:
    name: java_user
    comment: User java
    group: admin
    password: $2y$10$WFcmKfaWY/LaKYXFXZXVyuXBnYq9S2wm4xRNTA0nKuYwg6fHwE.lu
```

on télécharge l'application java,

```
- name: download java app
  become: yes
  get_url:
    url: https://github.com/Ozz007/sb3t/releases/download/1.0/sb3t-ws-1.0.jar
    dest: /home/java_user/
```

et on lance l'application.

```
- name: Run Jar
  shell: 'java -jar {{ JAR_PATH }}/{{ JAR_NAME }} -Xmx{{ JVM_SIZE }}'
  become_user : '{{ USER_NAME }}'
  async: 100
```

On peut lancer nos taches ainsi créées grace à notre playbook.

La pipeline jenkins nous permet de configurer l'instance.
