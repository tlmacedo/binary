package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "ContaToken")
@Table(name = "conta_token")
public class ContaToken implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    StringProperty descricao = new SimpleStringProperty();
    StringProperty tokenApi = new SimpleStringProperty();
    StringProperty moeda = new SimpleStringProperty();
    BooleanProperty cReal = new SimpleBooleanProperty();
    StringProperty email = new SimpleStringProperty();
    StringProperty senha = new SimpleStringProperty();
    BooleanProperty tokenAtivo = new SimpleBooleanProperty();

    public ContaToken() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    @Column(length = 80, nullable = false)
    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
    }

    @Column(length = 120, nullable = false, unique = true)
    public String getTokenApi() {
        return tokenApi.get();
    }

    public StringProperty tokenApiProperty() {
        return tokenApi;
    }

    public void setTokenApi(String tokenApi) {
        this.tokenApi.set(tokenApi);
    }

    @Column(length = 12, nullable = false)
    public String getMoeda() {
        return moeda.get();
    }

    public StringProperty moedaProperty() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda.set(moeda);
    }

    @Column(length = 1, nullable = false)
    public boolean iscReal() {
        return cReal.get();
    }

    public BooleanProperty cRealProperty() {
        return cReal;
    }

    public void setcReal(boolean cReal) {
        this.cReal.set(cReal);
    }

    @Column(length = 150)
    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Column(length = 128)
    public String getSenha() {
        return senha.get();
    }

    public StringProperty senhaProperty() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha.set(senha);
    }

    @Column(length = 1, nullable = false)
    public boolean isTokenAtivo() {
        return tokenAtivo.get();
    }

    public BooleanProperty tokenAtivoProperty() {
        return tokenAtivo;
    }

    public void setTokenAtivo(boolean tokenAtivo) {
        this.tokenAtivo.set(tokenAtivo);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s_%s",
                iscReal() ? "***REAL***" : "Virtual",
                descricaoProperty().getValue(),
                moedaProperty().getValue());
    }
}
