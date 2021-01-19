package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Token")
@Table(name = "token")
public class Token implements Serializable {
    public static final Long serialVersionUID = 1L;

    private LongProperty id = new SimpleLongProperty();
    private StringProperty descricao = new SimpleStringProperty();
    private StringProperty token = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty senha = new SimpleStringProperty();
    private BooleanProperty valido = new SimpleBooleanProperty();

    public Token() {
    }

    //    public Tokens(String descricao, String email, String senha) {
//        this.descricao = new SimpleStringProperty(descricao);
//        this.email = new SimpleStringProperty(email);
//        this.senha = new SimpleStringProperty(senha);
//    }

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

    @Column(length = 150, nullable = false)
    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
    }

    @Column(length = 100, nullable = false, unique = true)
    public String getToken() {
        return token.get();
    }

    public StringProperty tokenProperty() {
        return token;
    }

    public void setToken(String token) {
        this.token.set(token);
    }

    @Column(length = 150, nullable = false)
    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    @Column(length = 128, nullable = false)
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
    public boolean isValido() {
        return valido.get();
    }

    public BooleanProperty validoProperty() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido.set(valido);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)",
                descricaoProperty().getValue(),
                tokenProperty().getValue());
    }
}
