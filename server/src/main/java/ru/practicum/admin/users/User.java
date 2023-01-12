package ru.practicum.admin.users;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ewm_user")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(length = 50, unique = true, nullable = false)
    private String email;
    @Column(length = 250, unique = true, nullable = false)
    private String name;
}
