package exam;

public class ExamRoom {
    private int roomId;
    private String examCode;
    private String siteCode;
    private String siteName;
    private String roomCode;
    private String roomLocation;
    private int seatNum;
    private byte needSignIn;

    public ExamRoom() {}

    public ExamRoom(String siteCode, String roomCode) {
        this.siteCode = siteCode;
        this.roomCode = roomCode;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomLocation() {
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public byte getNeedSignIn() {
        return needSignIn;
    }

    public void setNeedSignIn(byte needSignIn) {
        this.needSignIn = needSignIn;
    }
}
