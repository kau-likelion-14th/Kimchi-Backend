package likelion14th.lte.user.entity;

import jakarta.persistence.*;
import likelion14th.lte.follow.entity.Follow;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.statistic.entity.Statistic;
import likelion14th.lte.youtube.domain.SavedSong;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(length = 16, nullable = false, unique = true)
    private String userTag;
    @Column(columnDefinition = "TEXT")
    private String introduction;
    @Column(columnDefinition = "TEXT")
    private String profileImage;
    @Column(columnDefinition = "TEXT")
    private String s3ImageKey;

    @OneToMany(mappedBy = "toUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers;

    @OneToMany(mappedBy = "fromUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<SavedSong> savedSongs;

    @Builder(access = AccessLevel.PUBLIC)
    private User (String username, String userTag, String introduction, String s3ImageKey, String profileImage){
        this.username = username;
        this.userTag = userTag;
        this.introduction = introduction;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.statistic = Statistic.createDefault();
        this.savedSongs = new ArrayList<>();
        this.s3ImageKey = s3ImageKey;
        this.profileImage = profileImage;
    }
    public void fixUserProfile(String s3ImageUrl, String s3ImageKey){
        this.s3ImageKey = s3ImageKey;
        this.profileImage = s3ImageUrl;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }
}