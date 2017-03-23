package Entity;

import java.io.Serializable;

/**
 * Created by S.R Rain on 1/11/2016.
 */
public class Agent implements Serializable, Comparable<Agent> {


    public long agentId;
    public String agentThumbnailUrl;
    public String name;
    public String ProfileSummary;
    public String AgentLicenseNumber;
    public int UserCategoryID;
    public String Position;
    public String CompanyName;
    public String mobile;
    public String email;
    public String Facebook;
    public String Twitter;
    public String LinkedIn;
    public String GooglePlus;
    public String country;
    public String state;
    public String city;
    public String locality;
    public String fulladdress;
    public String address;
    public String zipCode;
    public String OfficePhone;
    public String PersonalBlogAddress;
    public String PhotoFileName;
    public Boolean verified;
    public int ActiveAgentCount;
    public int recommendationCount;
    public String agentPermaLink;
    public String CompanyLogoUrl;
    public String CompanyPermalink;
    public String MemberSince;
    public String CreateDate;
    public int RowNumber;
    public String TotalCount;
    public int ContactID;
    public int CompanyID;
    public String DisplayID;
    public double Latitude;
    public double Longitude;
    public int activeListing;
    public boolean IsLiked = false;


    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Agent o) {
        return getName().compareTo(o.getName());
    }

}
