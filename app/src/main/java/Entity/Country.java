package Entity;

/**
 * Created by iBaax on 2/7/16.
 */
public class Country {

    public String ID;
    public String CountryID;
    public String CountryTicker;
    public String Name;
    public String PhoneCode;
    public String CurrencyTicker;
    public double lat;
    public double lon;

    public Country() {

    }

    public Country(String CountryID, String CountryTicker, String Name, String PhoneCode, String CurrencyTicker, double lat, double lon) {

        this.CountryID = CountryID;
        this.CountryTicker = CountryTicker;
        this.Name = Name;
        this.PhoneCode = PhoneCode;
        this.CurrencyTicker = CurrencyTicker;
        this.lat = lat;
        this.lon = lon;

    }
}
