package se.inera.intyg.intygsbestallning.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "HANDLAGGARE")
public class Handlaggare {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "FULLSTANDIGT_NAMN")
    private String fullstandigtNamn;

    @Column(name = "TELEFONNUMMER")
    private String telefonnummer;

    @Column(name = "EMAIL")
    private String email;

    // TODO Enum? NAMN?!
    @Column(name = "AUTHORITY")
    private String authority;

    @Column(name = "KONTOR")
    private String kontor;

    // TODO Vad ska jag kalla denna?!
    @Column(name = "KONTOR_COST_CENTER")
    private String kontorCostCenter;

    @Column(name = "ADRESS")
    private String adress;

    @Column(name = "POSTKOD")
    private String postkod;

    @Column(name = "STAD")
    private String stad;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullstandigtNamn() {
        return fullstandigtNamn;
    }

    public void setFullstandigtNamn(String fullstandigtNamn) {
        this.fullstandigtNamn = fullstandigtNamn;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getKontor() {
        return kontor;
    }

    public void setKontor(String kontor) {
        this.kontor = kontor;
    }

    public String getKontorCostCenter() {
        return kontorCostCenter;
    }

    public void setKontorCostCenter(String kontorCostCenter) {
        this.kontorCostCenter = kontorCostCenter;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPostkod() {
        return postkod;
    }

    public void setPostkod(String postkod) {
        this.postkod = postkod;
    }

    public String getStad() {
        return stad;
    }

    public void setStad(String stad) {
        this.stad = stad;
    }
}
