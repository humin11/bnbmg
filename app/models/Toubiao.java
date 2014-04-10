package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Humin on 4/3/14.
 */
@Entity
public class Toubiao extends Model {

    @ManyToOne
    public Request request;

    @OneToOne
    public Profile profile;

    public String price;

    public String totalPrice;

    public String payStyle;

    public String invoice;

    public String comments;

    public String againComments;

    @OneToOne
    public User user;

    public Boolean again=false;

    /**
     * 0 - toubiao
     * 1 - zhong
     * 2 - weizhong
     *
     */
    public String status;

    @OneToMany
    public List<Baojia> baojias = new ArrayList<Baojia>();

}
