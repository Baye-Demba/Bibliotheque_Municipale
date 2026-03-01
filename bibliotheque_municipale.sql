DROP DATABASE IF EXISTS bibliotheque_municipale;
CREATE DATABASE bibliotheque_municipale CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque_municipale;



CREATE TABLE utilisateurs (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    login              VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe       VARCHAR(255) NOT NULL,
    nom                VARCHAR(100),
    prenom             VARCHAR(100),
    email              VARCHAR(150),
    profil             ENUM('ADMIN','BIBLIOTHECAIRE') NOT NULL DEFAULT 'BIBLIOTHECAIRE',
    actif              TINYINT(1) NOT NULL DEFAULT 1,
    date_creation      DATETIME DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion DATETIME,
    premiere_connexion TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    libelle     VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE livres (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    isbn               VARCHAR(20),
    titre              VARCHAR(200) NOT NULL,
    auteur             VARCHAR(150) NOT NULL,
    categorie_id       INT,
    annee_publication  INT,
    nombre_exemplaires INT NOT NULL DEFAULT 1,
    disponible         TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE adherents (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    matricule        VARCHAR(30) NOT NULL UNIQUE,
    nom              VARCHAR(100) NOT NULL,
    prenom           VARCHAR(100) NOT NULL,
    email            VARCHAR(150),
    telephone        VARCHAR(20),
    adresse          VARCHAR(255),
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    actif            TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE emprunts (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    livre_id              INT NOT NULL,
    adherent_id           INT NOT NULL,
    utilisateur_id        INT,
    date_emprunt          DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_retour_prevue    DATE NOT NULL,
    date_retour_effective DATE,
    penalite              DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (livre_id)       REFERENCES livres(id),
    FOREIGN KEY (adherent_id)    REFERENCES adherents(id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL
);



INSERT INTO utilisateurs (login, mot_de_passe, nom, prenom, email, profil, actif, premiere_connexion) VALUES
('admin',   'admin123', 'Administrateur', 'Systeme',      'admin@biblio-dakar.sn', 'ADMIN',          1, 1),
('ByDemba', 'passer@1', 'Gadiaga',        'Demba Wagne',  'bydemba@gmail.com',     'BIBLIOTHECAIRE', 1, 0);



INSERT INTO categories (libelle, description) VALUES
('Histoire',      'Histoire africaine et mondiale'),
('Informatique',  'Programmation, reseaux, cybersecurite'),
('Litterature',   'Romans et oeuvres litteraires'),
('Economie',      'Economie generale et finance'),
('Science',       'Sciences exactes et naturelles'),
('Philosophie',   'Philosophie africaine et classique'),
('Mathematiques', 'Algebre, analyse, probabilites');



INSERT INTO livres (titre, auteur, categorie_id, annee_publication, nombre_exemplaires, disponible) VALUES
('Le modele economique mouride',
    'Cheikh Gueye',
    (SELECT id FROM categories WHERE libelle = 'Histoire'),
    NULL, 3, 1),

('La Cybersecurite et la Souverainete Numerique au Senegal',
    'Gerard Joseph Francisco Dacosta',
    (SELECT id FROM categories WHERE libelle = 'Informatique'),
    2025, 15, 1),

('Une si longue lettre',
    'Mariama Ba',
    (SELECT id FROM categories WHERE libelle = 'Litterature'),
    1979, 37, 1),

('Pere Riche, Pere Pauvre',
    'Robert Kiyosaki',
    (SELECT id FROM categories WHERE libelle = 'Economie'),
    1997, 5, 1),

('Le Guide du Genie Logiciel',
    'Pr Moustapha Der',
    (SELECT id FROM categories WHERE libelle = 'Informatique'),
    2023, 100, 1);



INSERT INTO adherents (matricule, nom, prenom, email, telephone, adresse, actif) VALUES
('ADH-2025-001', 'Ndiaye', 'Omar',     'Ozmocisko@gmail.com',  '768308212', 'Golf',       1),
('ADH-2025-002', 'Ly',     'Amadou',   'am21@gmail.com',       '768308212', 'Mbadakhoune',1),
('ADH-2025-003', 'Sene',   'Aissatou', 'ibrafalienne@gmail.com','780183908', 'Bambey',     1),
('ADH-2025-004', 'Diome',  'Aissatou', 'chacha@gmail.com',     '768308212', 'Lagnar',     1),
('ADH-2025-005', 'Ndiaye', 'Binta',    'goddess@gmail.com',    '768308212', 'Fongolomi',  1),
('ADH-2025-006', 'Gueye',  'Mouhamed', 'mhd@gmail.com',        '768308212', 'Poute',      1),
('ADH-2025-007', 'Sall',   'Marieme',  'sounade@yahoo.fr',     '768308212', 'ToubaCouta', 1);



INSERT INTO emprunts (livre_id, adherent_id, utilisateur_id, date_emprunt, date_retour_prevue) VALUES
(3, 1, 2, NOW() - INTERVAL 3  DAY, DATE_ADD(CURDATE(), INTERVAL 11 DAY)),
(4, 2, 2, NOW() - INTERVAL 7  DAY, DATE_ADD(CURDATE(), INTERVAL  7 DAY)),
(1, 3, 2, NOW() - INTERVAL 1  DAY, DATE_ADD(CURDATE(), INTERVAL 13 DAY)),
(5, 4, 2, NOW() - INTERVAL 10 DAY, DATE_ADD(CURDATE(), INTERVAL  4 DAY)),
(2, 5, 2, NOW() - INTERVAL 2  DAY, DATE_ADD(CURDATE(), INTERVAL 12 DAY));


UPDATE livres SET disponible = 0 WHERE id IN (1, 2, 3, 4, 5);

