package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GoogleLoginRequest {
    private String idToken;
    private int grade;
    private String birthDate;
}