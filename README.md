Projet de Bibliotheque Municipale de Dakar
Demba Wagne GADIAGA & Papa Meissa NDIAYE - L3 Reseau Informatique

1- On a creer notre page de login avec login.fxml
2- on a ajouter les autres pages necessaires (dashboard, Utilisateurs, Adherants, livres, emprunts, change_mdp.fxml)

INSTALLATION
------------
1. Installer MySQL et creer la base : importer bibliotheque_municipale.sql
2. Verifier DB.java : url = jdbc:mysql://127.0.0.1:3306/bibliotheque_municipale, user = root, mdp = ""
3. Lancer : mvn clean javafx:run

COMPTES PAR DEFAUT
------------------
Admin     : login = admin    / mdp = admin123  (changement de mdp obligatoire a la 1ere connexion)
Biblio    : login = ByDemba  / mdp = passer@1

CREATION DE SETUP
------------------
On est allé vers le Maven double cliquer sur Clean on attends qu'il construit et si on a BUILD SUCCESS on double clique sur Package on attend le même resultat Il crée un nouveau document target avec des données à l'interieur 
