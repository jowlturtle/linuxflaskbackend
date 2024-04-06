package codehanzoom.greenwalk.user.controller;


import codehanzoom.greenwalk.global.dto.ResponseDto;
import codehanzoom.greenwalk.global.dto.UserJoinDto;
import codehanzoom.greenwalk.user.repository.UserRepository;
import codehanzoom.greenwalk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping({"","/"})
    public String index()  {
        return "<h1>index</h1>";
    }

    // ROLE_USER의 회원가입 경로
    @PostMapping("/auth/join")
    public ResponseDto<String> join(@RequestBody UserJoinDto userJoinDto) throws Exception {
        userService.join(userJoinDto);
        return new ResponseDto<String>(HttpStatus.OK.value(), "회원가입 완료");
    }

    // jwt Token 전송 여부 확인 경로(임시)
    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
