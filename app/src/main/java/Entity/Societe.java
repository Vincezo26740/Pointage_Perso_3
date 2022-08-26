package Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Societe {
    @PrimaryKey(autoGenerate = true)
    public int id;
    int CP;
    String name, address, town;
    String email, email2, email3;

    public Societe(int CP, String name, String address, String town, String email) {
        this.CP = CP;
        this.name = name;
        this.address = address;
        this.town = town;
        this.email = email;
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

    public String getEmail2() {
        return email2;
    }

    public String getEmail3() {
        return email3;
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

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public void setEmail3(String email3) {
        this.email3 = email3;
    }
}
