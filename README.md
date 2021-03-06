# StarBus, application Java FX

StarBus est une interface graphique JAVA FX indiquant les prochains passages de bus en temps réels aux arrêts de bus du réseau Star de Rennes.
Elle a été designée pour tourner sur **Raspberry Pi 3** et + équipés d'**écrans tactiles 7 pouces** (voir section installation) mais peut-être installée sur PC.

## Fonctionnalités

L'application a besoin d'un connection internet pour fonctionner car elle utilise les données de l'**API** officielle de la **Star** https://data.explore.star.fr/explore/?sort=title

![alt text](https://github.com/PaulBorie/StarBus/blob/master/gitimages/bus.png?raw=true)

La fenêtre principale indique les **prochains passages** en minutes des bus à un arrêt de bus du réseau Star. Elle dispose de 3 boutons, une flèche en bas à droite permettant de passer à l'**arrêt suivant**, une croix en bas au milieu permettant de **supprimer** l'arrêt de bus courrament affiché de la liste des arrêts de bus à afficher et un bouton soleil en bas à gauche permattant d'afficher la **météo** à Rennes.

![alt text](https://github.com/PaulBorie/StarBus/blob/master/gitimages/meteo.png?raw=true)


# Installation

## Desktop version for Windows, Linux and Mac 

With a **Java RunTime Environment version >= 11**

```bash
git clone https://github.com/PaulBorie/StarBus.git StarBus
cd StarBus/INSTALL/DESKTOP/
./launch.sh
``` 

## Rasperry pi version

You need first to execute the `install.sh` script which will install the right Version of Java Development Kit with the **Java runtime envrironment** in it for **ARM processor 32 bits** (for devices  like Raspberry) As JavaFX is no longer part of the Java JDK (since version 11), running a JavaFX program on Raspberry Pi will not work out-of-the-box. Luckily we can use the **LibericaJDK** which is provided by BellSoft. They have a version dedicated for the Raspberry Pi which includes JavaFX.

```bash
git clone https://github.com/PaulBorie/StarBus.git StarBus 
cd StarBus/INSTALL/RASPBERRY/
./install.sh
./launch.sh
``` 

## Ajouter des arrêts à afficher

L'application fait tourner un serveur en background écoutant les requêtes clientes sur le port 9999 sur le réseau local.
Les requêtes envoyées permettent :
* D'ajouter des arrêts avec des requêtes de la forme `ADD/nom_arret/numero_ligne/direction`
* De supprimer des arrêts avec des requêtes de la forme  `REMOVE/nom_arret/numero_ligne/direction`
* De Changer la ville dont on affiche la météo `SETCITY/city`

*Une application mobile cliente est en cours de developpement implémentant ces requêtes au travers d'une interface graphique simple pour l'utilisateur qui sera alors agnostique de ces requêtes. L'utilisateur aura juste à télécharger l'application, connecter son téléphone sur le même réseau wifi que son RaspBerry, à synchroniser le téléphone et le RaspBerry à l'aide d'un bouton sync et de sélectionner les arrêts de bus à ajouter/supprimer à l'aide de l'interface graphique.*

On peut tout de même déjà tester l'application à l'aide de petits clients réseau pré-installés sur beaucoup de distributions : `netcat` ou `telnet` cependant les numéros de ligne, noms des arrêts dans les commandes envoyées doivent respecter rigoureusement la syntaxe fournie par l'API Star indiquée dans le lien suivant : https://data.explore.star.fr/explore/dataset/tco-bus-circulation-passages-tr/api/

* Exemple si l'application tourne sur une machine dont l'ip privée est `192.168.1.22`, alors une machine du même réseau peut envoyer des requêtes à l'application à l'aide de `netcat` : (on peut aussi tester sur la même machine sur laquelle tourne l'application en remplaçant `192.168.1.22` par `localhost` dans les commandes suivantes):

```bash
echo "ADD/Turmel/C3/Saint-Laurent" | netcat -q 1 192.168.1.22 9999
```

```bash
echo "REMOVE/Dargent/9/Cleunay" | netcat -q 1 192.168.1.22 9999
``` 

```bash
echo "SETCITY/Nantes" | netcat -q 1 192.168.1.22 9999
``` 

On peut également modifier le fichier `config.txt` en ajoutant des lignes `ADD/nom_arret/numero_ligne/direction` pour ajouter des arrêts manuellement. Ce fichier est lu au **lancement** de l'application et les arrêts nouvellement ajoutés durant le temps d'exécution de l'application sont **sauvegardés** ici lors de la fermeture de l'application. 


## Raccourcis

* `f` pour mettre l'applicaiton en fullscreen
* `CTRL + c` pour fermer l'applciation 




