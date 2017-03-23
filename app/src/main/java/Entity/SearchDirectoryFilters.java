package Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iBaax on 5/2/16.
 */
public class SearchDirectoryFilters implements Parcelable {

    public String DirectoryType;
    public String QueryString;
    public String Specialities;
    public String AreaCoverage;
    public String SpokenLanguages;

    public SearchDirectoryFilters() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.DirectoryType);
        dest.writeString(this.QueryString);
        dest.writeString(this.Specialities);
        dest.writeString(this.AreaCoverage);
        dest.writeString(this.SpokenLanguages);
    }

    protected SearchDirectoryFilters(Parcel in) {
        this.DirectoryType = in.readString();
        this.QueryString = in.readString();
        this.Specialities = in.readString();
        this.AreaCoverage = in.readString();
        this.SpokenLanguages = in.readString();
    }

    public static final Creator<SearchDirectoryFilters> CREATOR = new Creator<SearchDirectoryFilters>() {
        @Override
        public SearchDirectoryFilters createFromParcel(Parcel source) {
            return new SearchDirectoryFilters(source);
        }

        @Override
        public SearchDirectoryFilters[] newArray(int size) {
            return new SearchDirectoryFilters[size];
        }
    };
}
