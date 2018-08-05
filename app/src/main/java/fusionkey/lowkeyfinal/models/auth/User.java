package fusionkey.lowkeyfinal.models.auth;

public class User {
    // identification data
    private String email;
    private String username;

    // personalisation
    private String photo;
    private String bio;
    private String gender; // let's abuse the guy - woman psychology

    //score ( let's make a level system that will encourage the users)
    private int score;
    private int level;

    public User(String email, String username, String photo, String bio, String gender) {
        this.email = email;
        this.username = username;
        this.photo = photo;
        this.bio = bio;
        this.gender = gender;
        this.score = 0;
        this.level = 0;
    }
}
