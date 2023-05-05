# TrainGo Backend

Ce répertoire git contient tout le code source du backend de TrainGo

## Comment compiler

Pour compiler le serveur, il vous faudra:
  - java
    - java >= 17.0
  - [gradle]("https://gradle.org/install/")
    - gradle >= 8.0.0 

et de lancer la commande:
```
$ git clone https://gaufre.informatique.univ-paris-diderot.fr/lefrancm/gla-calcul-itineraire
$ cd gla-calcul-itineraire
$ ./gradlew build
```

Vous trouverez le ficher dans le dossier ```gla-calcul-itineraire/build/libs/gla-calcul-itineraire-all.jar ```

## Utilisation
Dans le fichier de config, définir le port

Lancez l'application en utilisant :
```
$ java -jar <executatble en .jar> <mapData> [timeData]
```

## Console
Une fois le serveur démarré, une invite de commande est mis à votre disposition vous permettant d'effectuer les commandes suivantes:

### - Update-Map 
```
$ update-map <ficher>
```

Cette commande vous permet de changer le réseau

### - Update-Time 
```
$ update-time <ficher>
```

Cette commande vous permet de mettre-à-jour les horaires

### - Kill

```
$ kill
```
Cette commande permet d'arrêter le server

## Communication

### - Requêtes

Le server communique avec le client via des requêtes tcp suivant un protocole définit

#### ROUTE
La requête ```ROUTE``` permet de calculer le chemin entre deux stations, coordonnées ou un mélange des deux.

- **ROUTE;\<station>;\<station>;\<time>;DISTANCE;**
- **ROUTE;\<station>;\<station>;\<time>;TIME;**
- **ROUTE;\<station>;\<station>;\<time>;DISTANCE;FOOT**
- **ROUTE;<station>;\<station>;\<time>;TIME;FOOT**

#### SEARCH
La requête ```SEARCH``` permet d'obtenir la liste des stations ainsi que leur coorespondance commançant par un certain préfix.

- **SEARCH;<prefix>;<DEPART | ARRIVAL>**
 
#### TIME
La requête ```TIME``` permet d'obtenir la liste des horaires des trains.

- **TIME;\<prefix>;\<DEPART | ARRIVAL>** 

###  - Erreurs

En cas de requêtes mal formées, non reconnues, vides ou nulles, le serveur renvoie un objet ```ServerError``` decrivant la raison de cette erreur.

