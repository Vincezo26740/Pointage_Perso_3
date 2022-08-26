package Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    int id;

    int CP;
    String name, address, town;
    String email, firstName,  PW;

    public User(String name, String email, String address, int CP, String town,  String PW) {
        this.CP = CP;
        this.name = name;
        this.address = address;
        this.town = town;
        this.email = email;
        this.PW = PW;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", CP=" + CP +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", town='" + town + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", PW='" + PW + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getCP() {
        return CP;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTown() {
        return town;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPW() {
        return PW;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCP(int CP) {
        this.CP = CP;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

}
