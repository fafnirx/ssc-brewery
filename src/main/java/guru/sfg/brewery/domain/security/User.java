package guru.sfg.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String userName;
    private String passWord;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles;

    @Transient
    private Set<Authority> authorities;

    /*
    Collect all authorities from all roles in one set.
     */
    public Set<Authority> getAuthorities() {
        return roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Builder.Default
    private Boolean accountNotExpired = true;
    @Builder.Default
    private Boolean accountNotLocked = true;
    @Builder.Default
    private Boolean credentialsNotExpired = true;
    @Builder.Default
    private Boolean enabled = true;

}
