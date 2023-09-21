package fr.LCB.pointageperso3.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Lieu{

    @PrimaryKey(autoGenerate = true)
    public int id;
    int CP;
    String name, address, town;
    float longitude, latitude;

    public Lieu(int CP, String name, String address, String town) {
        this.CP = CP;
        this.name = name;
        this.address = address;
        this.town = town;
    }

    public int getId() {return id;}
    public int getCP() {return CP;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String getTown() {return town;}
    public float getLongitude() {return longitude;}
    public float getLatitude() {return latitude;}

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

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
