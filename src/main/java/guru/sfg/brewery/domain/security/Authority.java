package guru.sfg.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String role;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
