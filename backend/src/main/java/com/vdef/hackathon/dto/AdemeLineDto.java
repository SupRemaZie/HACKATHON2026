package com.vdef.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AdemeLineDto {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("Identifiant_de_l_élément")
    private String identifiant;

    @JsonProperty("Nom_base_français")
    private String nomFrancais;

    @JsonProperty("Nom_attribut_français")
    private String nomAttributFrancais;

    @JsonProperty("Unité_français")
    private String unite;

    @JsonProperty("Total_poste_non_décomposé")
    private Double totalPosteNonDecompose;

    @JsonProperty("Type_poste")
    private String typePoste;

    @JsonProperty("Catégorie")
    private String categorie;

    @JsonProperty("Source")
    private String source;

    @JsonProperty("Année_de_Publication")
    private String anneePublication;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdentifiant() { return identifiant; }

    public String getNomFrancais() { return nomFrancais; }
    public void setNomFrancais(String nomFrancais) { this.nomFrancais = nomFrancais; }

    public String getNomAttributFrancais() { return nomAttributFrancais; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public Double getTotalPosteNonDecompose() { return totalPosteNonDecompose; }
    public void setTotalPosteNonDecompose(Double totalPosteNonDecompose) { this.totalPosteNonDecompose = totalPosteNonDecompose; }

    public String getTypePoste() { return typePoste; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getAnneePublication() { return anneePublication; }
    public void setAnneePublication(String anneePublication) { this.anneePublication = anneePublication; }
}
