package guitartex2;

public class ServerResponse {
    private int _urlRC = -1;
    private int _cmdRC = -1;
    private String _msg = "";
    private String _downloadURL = "";

    public int getUrlRC() {
        return _urlRC;
    }
    public void setUrlRC(int urlRC) {
        _urlRC = urlRC; 
    }
    public int getCmdRC() {
        return _cmdRC;
    }
    public void setCmdRC(int cmdRC) {
        _cmdRC = cmdRC;
    }
    public String getMsg(){
        return _msg;
    }
    public void setMsg(String msg){
        _msg = msg;
    }
    public String getDownloadURL(){
        return _downloadURL;
    }
    public void setDownloadURL(String downloadString ){
        _downloadURL = downloadString;
    } 

    public boolean isInitial(){
        if ( _urlRC == -1 && _cmdRC == -1 && _msg == "" && _downloadURL == "" ) {
            return true;
        }else{
            return false;
        }
    }

    public String toString() {
        StringBuilder myresponse = new StringBuilder();
        myresponse.append("urlRC: " + _urlRC + "\n");
        myresponse.append("cmdRC: " + _cmdRC + "\n");
        myresponse.append("msg: " + _msg + "\n");
        myresponse.append("DownloadURL: " + _downloadURL + "\n");
        return myresponse.toString();
    }
}
