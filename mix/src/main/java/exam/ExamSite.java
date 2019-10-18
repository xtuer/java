package exam;

import java.util.List;

/**
 * Created by Administrator on 2016/1/8.
 */

public class ExamSite {
    private int siteId;
    private String siteCode;
    private String siteName;
    private String siteAddress;
    private int roomNum;
    private String sitePassword;
    private String ipScope;
    private String examCode;
    private String userName;
    private List<ExamRoom> examRoomList;

    public ExamSite() {
    }

    public ExamSite(String examCode, String siteCode) {
        this.examCode = examCode;
        this.siteCode = siteCode;
    }

    public ExamSite(String userName, String examCode, String siteCode, String siteName, String sitePassword) {
        this.userName = userName;
        this.examCode = examCode;
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.sitePassword = sitePassword;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public String getSitePassword() {
        return sitePassword;
    }

    public void setSitePassword(String sitePassword) {
        this.sitePassword = sitePassword;
    }

    public String getIpScope() {
        return ipScope;
    }

    public void setIpScope(String ipScope) {
        this.ipScope = ipScope;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public List<ExamRoom> getExamRoomList() {
        return examRoomList;
    }

    public void setExamRoomList(List<ExamRoom> examRoomList) {
        this.examRoomList = examRoomList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "ExamSite{" +
                "siteCode='" + siteCode + '\'' +
                ", sieName='" + siteName + '\'' +
                ", siteAddress='" + siteAddress + '\'' +
                '}';
    }
}
