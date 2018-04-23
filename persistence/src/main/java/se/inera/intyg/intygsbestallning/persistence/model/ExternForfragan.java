package se.inera.intyg.intygsbestallning.persistence.model;

import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EXTERN_FORFRAGAN")
public class ExternForfragan {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "LANDSTING_HSA_ID", nullable = false)
    private String landstingHsaId;

    @Column(name = "BESVARAS_SENAST_DATUM", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime besvarasSenastDatum;

    @Column(name = "KOMMENTAR")
    private String kommentar;

    @Column(name = "AVVISAT_KOMMENTAR")
    private String avvisatKommentar;

    @Column(name = "AVVISAT_DATUM")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    private LocalDateTime avvisatDatum;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERN_FORFRAGAN_ID", referencedColumnName = "ID", nullable = false)
    private List<InternForfragan> internForfraganList = new ArrayList<>();

    public String getLandstingHsaId() {
        return landstingHsaId;
    }

    public void setLandstingHsaId(String landstingHsaId) {
        this.landstingHsaId = landstingHsaId;
    }

    public String getAvvisatKommentar() {
        return avvisatKommentar;
    }

    public void setAvvisatKommentar(String avvisatKommentar) {
        this.avvisatKommentar = avvisatKommentar;
    }

    public LocalDateTime getAvvisatDatum() {
        return avvisatDatum;
    }

    public void setAvvisatDatum(LocalDateTime avvisatDatum) {
        this.avvisatDatum = avvisatDatum;
    }

    public LocalDateTime getBesvarasSenastDatum() {
        return besvarasSenastDatum;
    }

    public void setBesvarasSenastDatum(LocalDateTime besvarasSenastDatum) {
        this.besvarasSenastDatum = besvarasSenastDatum;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public List<InternForfragan> getInternForfraganList() {
        return internForfraganList;
    }

    public void setInternForfraganList(List<InternForfragan> internForfraganList) {
        this.internForfraganList = internForfraganList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
