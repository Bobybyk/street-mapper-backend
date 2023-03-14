# Scenarios des cas d'utilisation

**Acteurs :** client de l'application

**But d'utilisation :** obtenir, à partir d'un point de départ et d'arrivé, un itinéraire détaillé et aussi court que possible

## Scénario 1

1. L'utilisateur renseigne dans un champ l'adresse d'arrivée
2. L'utilisateur renseigne dans un champ l'adresse de départ *(**bis :** l'utilisateur ajoute d'autre points d'étapes)*
3. L'utilisateur valide le formulaire
4. L'application détermine les coordonnées GPS des adresses renseignées
5. L'application calcul l'itinéraire le plus court entre chaque point
6. L'application affiche l'itinéraire sur une carte

## Scénario 2

1. L'utilisateur navigue dans les options de l'application
2. L'utilisateur accède à son historique d'itinéraire
3. L'application affiche la liste des itinéraires précédemment calculés
4. L'utilisateur sélectionne un itinéraire
5. L'application affiche l'itinéraire sur une carte

## Scénario 3

1. L'utilisateur navigue dans les options de l'application
2. L'utilisateur renseigne ses lignes (de bus ? de métro ?) préférées
3. L'application enregistre les lignes à privilégier avant de calculer l'itinéraire

## Scénario 4

1. L'utilisateur renseigne une adresse de départ et une adresse d'arrivée
2. L'utilisateur valide le formulaire
3. L'application détermine les coordonnées GPS des adresses renseignées
4. L'application calcul l'itinéraire le plus court entre chaque point
5. L'utilisateur sélectionne cet itinéraire comme son favoris
6. L'application enregistre l'itinéraire dans la liste des favoris de l'utilisateur