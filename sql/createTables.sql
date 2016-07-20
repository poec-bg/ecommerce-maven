CREATE TABLE `Client` (
  `id` VARCHAR(255) NOT NULL,
  `nom` VARCHAR(45) NULL,
  `email` VARCHAR(45) NOT NULL,
  `motDePasse` VARCHAR(45) NOT NULL,
  `prenom` VARCHAR(45) NULL,
  `adressePostale` VARCHAR(45) NULL,
  `telephone` VARCHAR(45) NULL,
  `isSupprime` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`, `email`, `motDePasse`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));

CREATE TABLE `Produit` (
  `id` VARCHAR(255) NOT NULL,
  `nom` VARCHAR(45) NULL,
  `description` VARCHAR(45) NULL,
  `prixUnitaire` FLOAT NULL,
  `isSupprime` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));

CREATE TABLE `Panier` (
  `id` VARCHAR(255) NOT NULL,
  `date` DATETIME NOT NULL,
  `idClient` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idClient_UNIQUE` (`idClient` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));

CREATE TABLE `ProduitPanier` (
  `idPanier` VARCHAR(255) NOT NULL,
  `idProduit` VARCHAR(255) NOT NULL,
  `quantite` INT NOT NULL,
  PRIMARY KEY (`idPanier`, `idProduit`));
