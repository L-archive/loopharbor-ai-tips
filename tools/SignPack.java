import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * AI 팁 묶음 서명 도구 (LoopHarbor를 만드는 사람만 쓴다). 실행: java SignPack.java <명령> ...
 *   keygen <개인키 파일> <공개키 파일>   Ed25519 열쇠 한 쌍을 만든다. 개인키는 저장소 밖에 두고 누구에게도 보내지 않는다.
 *   sign   <개인키 파일> <pack.json>     pack.json.sig 를 만든다(바이트 그대로 서명, 서명 뒤 pack.json 을 고치면 무효).
 *   verify <공개키 파일> <pack.json>     앱과 같은 방식으로 확인한다.
 *   sign-env <환경변수 이름> <pack.json> 개인키를 파일 대신 환경변수에서 읽는다(GitHub Actions의 비밀값용). 값은 출력하지 않는다.
 */
public class SignPack {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) { System.err.println("사용법: keygen <개인키> <공개키> | sign <개인키> <pack.json> | verify <공개키> <pack.json>"); System.exit(2); }
        switch (args[0]) {
            case "keygen" -> {
                Path privateFile = Path.of(args[1]), publicFile = Path.of(args[2]);
                if (Files.exists(privateFile)) { System.err.println("개인키 파일이 이미 있어요. 덮어쓰지 않아요."); System.exit(3); }
                KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
                Files.writeString(privateFile, Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()) + "\n", StandardCharsets.US_ASCII);
                Files.writeString(publicFile, Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()) + "\n", StandardCharsets.US_ASCII);
                System.out.println("만들었어요. 공개키 파일만 agent-app/src/main/resources/ai-tips/public-key.txt 로 복사하세요.");
            }
            case "sign" -> {
                PrivateKey key = KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(Files.readString(Path.of(args[1])).strip())));
                Path pack = Path.of(args[2]);
                Signature s = Signature.getInstance("Ed25519"); s.initSign(key); s.update(Files.readAllBytes(pack));
                Files.writeString(Path.of(pack + ".sig"), Base64.getEncoder().encodeToString(s.sign()) + "\n", StandardCharsets.US_ASCII);
                System.out.println("서명했어요: " + pack + ".sig");
            }
            case "sign-env" -> {
                String value = System.getenv(args[1]);
                if (value == null || value.isBlank()) { System.err.println("환경변수에 개인키가 없어요: " + args[1]); System.exit(3); }
                PrivateKey key = KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(value.strip())));
                Path pack = Path.of(args[2]);
                Signature s = Signature.getInstance("Ed25519"); s.initSign(key); s.update(Files.readAllBytes(pack));
                Files.writeString(Path.of(pack + ".sig"), Base64.getEncoder().encodeToString(s.sign()) + "\n", StandardCharsets.US_ASCII);
                System.out.println("서명했어요: " + pack + ".sig");
            }
            case "verify" -> {
                PublicKey key = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(Files.readString(Path.of(args[1])).strip())));
                Path pack = Path.of(args[2]);
                Signature s = Signature.getInstance("Ed25519"); s.initVerify(key); s.update(Files.readAllBytes(pack));
                boolean ok = s.verify(Base64.getDecoder().decode(Files.readString(Path.of(pack + ".sig")).strip()));
                System.out.println(ok ? "서명이 맞아요." : "서명이 맞지 않아요."); System.exit(ok ? 0 : 1);
            }
            default -> { System.err.println("모르는 명령: " + args[0]); System.exit(2); }
        }
    }
}
