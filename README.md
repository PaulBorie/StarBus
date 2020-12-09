# StarBus 

StarBus est une interface graphique minimale indiquant les prochains passages de bus en temps réels aux arrêts de bus du réseau Star de Rennes.
Elle a été designée pour tourner sur Raspberry Pi 3 et + équipés d'écrans tactiles 5 ou 7 pouces (voir section installation) mais peut-être installée sur PC.

## Fonctionnalités

L'application a besoin d'un connection internet pour fonctionner car elle utilise les données de l'API officielle de la Star : https://data.explore.star.fr/explore/?sort=title
![alt text](https://github.com/[username]/[reponame]/blob/[branch]/image.jpg?raw=true)

La fenêtre principale indique les prochains passages en minutes des bus à un arrêt de bus du réseau Star. elle dispose de 3 boutons, une flèche en bas à droite permettant de passer à l'arrêt suivant,
une croix au milieu permettant de supprimer cet arrêt de bus des arrêts affichés par cette application et un bouton soleil en bas à gauche permattant de passer à la deuxième fenêtre
affichant la météo à Rennes.

## ajouter des arrêts à afficher

Pour l'instant 

## Requirements

* A **Linux Virtual Private Server** (VPS) hosted in the Cloud or a server of your own (Cloud services providers usually give credits to new users)
* A **domain name** pointing to the **ip address** of this **VPS** : you can register one for free at https://www.freenom.com
* A **merchant account** at https://stripe.com and the stripe public api_key and private api_key provided by the account

## Installation/Deployment

Deploy a https **payment-ready** shopping website linked to your **domain name** in a few minutes by following these **commands lines** :

* Establish a connection to your **VPS** via **ssh** or other preferred manners and execute the following commands :

* If there is no user add a **user** :
```bash
sudo adduser username
```
* go to your **home folder (it's important because the script which will be launch lately depends of this location)** and clone this repository 

```bash
cd /home/username
git clone <thisrepository> webserver
```
* Go to the folder just created and run the `install.sh` script to install **Docker and Docker-Compose** if they are not already installed on your VPS

```bash
cd webserver
./install.sh
```
* Now that Docker and Docker-compose are intalled, you can run the `deployment.sh` script with 4 arguments : the `stripe_public_api_key`, the `stripe_private_ api_key`, the `domain_name` you registered for your website, an `email_address` for the SSL certificate registration.

```bash
./deployment.sh stipe_pub_api_key stripe_private_api_key domainname email
```
Now your website should be **availible** at **yourdomain.com** and you sould see the green locker indicating TLS encyption and HTTPS protocol while browsing it.
You can also process payment and you should see all the payments from the clients on your **Stripe Dashboard**. The SSL certificates will be automatically renewed everyday.

To check if the three Docker containers are running well: 
```
docker-compose ps
```
If you encounter any problems during the deployment, check the logs
```
docker-compose logs
```


