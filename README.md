# **TrainGo Backend**

![](/img/img_train_go.png " TrainGo")

Ce répertoire git contient tout le code source du backend de TrainGo

## **Comment compiler**

Pour compiler le serveur, il vous faudra :
  - java
    - java >= 17.0
  - [gradle]("https://gradle.org/install/")
    - gradle >= 8.0.0

Exécuter :
```
$ git clone https://gaufre.informatique.univ-paris-diderot.fr/lefrancm/gla-calcul-itineraire
$ cd gla-calcul-itineraire
$ ./gradlew build
```

Vous pouvez obtenir le ```.jar``` du serveur :
  - en compilant via la commande ci-dessus
  - à partir de la dernière release disponible

## **Utilisation**

Pour exécuter l'application depuis un terminal :
```
$ java -jar <executatble en .jar> <mapData path> [timeData]
```

## **Console**
Une fois le serveur démarré, une invite de commande est mis à votre disposition, vous permettant d'utiliser les commandes suivantes :

### *Update-Map*
```
$ update-map <fichier>
```

Cette commande vous permet de changer le plan

### *Update-Time*
```
$ update-time <fichier>
```

Cette commande vous permet de mettre-à-jour les horaires

### *Kill*

```
$ kill
```
Cette commande permet d'arrêter le server

## **Communication**

### Requêtes

Le server communique avec le client via des requêtes tcp suivant un protocole définit

#### *ROUTE*
La requête ```ROUTE``` permet de calculer le chemin entre deux stations, coordonnées ou un mélange des deux.

- **ROUTE;\<station>;\<station>;\<time>;DISTANCE;**
- **ROUTE;\<station>;\<station>;\<time>;TIME;**
- **ROUTE;\<station>;\<station>;\<time>;DISTANCE;FOOT**
- **ROUTE;\<station>;\<station>;\<time>;TIME;FOOT**

<station> peut être un nom de station ou une coordonnées GPS au format `(<latitude>,<longitude>)`
<time> est un horaire au format `(hh:mm)`

Le serveur répond par un objet ```Route```
#### *SEARCH*
La requête ```SEARCH``` permet d'obtenir la liste des stations ainsi que leur correspondance commençant par un certain préfixe.

- **SEARCH;\<prefix>;\<DEPART | ARRIVAL>**

Le serveur répond par un objet ```SuggestionStations```
#### *TIME*
La requête ```TIME``` permet d'obtenir la liste des horaires des trains.

- **TIME;\<station>;\<DEPART | ARRIVAL>**

Le serveur répond par un objet ```DepartureTimes```
###  - Erreurs

En cas de requêtes mal formées, non reconnues, vides ou nulles, le serveur renvoie un objet ```ServerError``` décrivant la raison de cette erreur.

## **Comment lancer les tests**
dans le dossier `gla-calcul-itineraire`
```
$ ./gradlew test
```