package com.softee5.mobil2team.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.softee5.mobil2team.config.GeneralException;
import com.softee5.mobil2team.config.ResponseCode;
import com.softee5.mobil2team.dto.*;
import com.softee5.mobil2team.entity.*;
import com.softee5.mobil2team.repository.*;
import com.vane.badwordfiltering.BadWordFiltering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private NicknameNounRepository nicknameNounRepository;
    @Autowired
    private NicknameModifierRepository nicknameModifierRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* 글 업로드 */
    public DataResponseDto<Void> uploadPost(PostDto postDto) {
        Post post = new Post();

        List<String> nounList = nicknameNounRepository.findAllNouns();
        List<String> modifierList = nicknameModifierRepository.findNicknameModifiersByTagId(postDto.getTagId());

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int idxModifier = random.nextInt(modifierList.size());
        int idxNoun = random.nextInt(nounList.size());
        String nickname = modifierList.get(idxModifier) + " " + nounList.get(idxNoun);

        // 비속어 처리
        String content = postDto.getContent();
        BadWordFiltering badWordFiltering = new BadWordFiltering();
        if (badWordFiltering.check(content)) {
            content = badWordFiltering.change(content);
        }

        post.setNickname(nickname);
        post.setContent(content);
        post.setLiked(0);

        post.setStation(Station.builder().id(postDto.getStationId()).build()); // 필수
        post.setTag(postDto.getTagId() != null && postDto.getTagId() != 0 ? Tag.builder().id(postDto.getTagId()).build() : null); // 선택
        post.setImage(postDto.getImageId() != null && postDto.getImageId() != 0 ? Image.builder().id(postDto.getImageId()).build() : null); // 선택

        postRepository.save(post);

        return DataResponseDto.of(null);
    }

    /* 사진 첨부 글 업로드 */
    public DataResponseDto<Void> uploadPostWithImage(PostDto postDto, MultipartFile file) {

        try {
            Post post = new Post();

            // 이미지 s3에 저장
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                imageUrl = saveFile(file);
            }

            List<String> nounList = nicknameNounRepository.findAllNouns();
            List<String> modifierList = nicknameModifierRepository.findNicknameModifiersByTagId(postDto.getTagId());

            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            int idxModifier = random.nextInt(modifierList.size());
            int idxNoun = random.nextInt(nounList.size());
            String nickname = modifierList.get(idxModifier) + " " + nounList.get(idxNoun);

            // 비속어 처리
            String content = postDto.getContent();
            BadWordFiltering badWordFiltering = new BadWordFiltering();
            if (badWordFiltering.check(content)) {
                content = badWordFiltering.change(content);
                System.out.println("content = " + content);
            }

            post.setNickname(nickname);
            post.setContent(content);
            post.setLiked(0);

            post.setStation(Station.builder().id(postDto.getStationId()).build()); // 필수
            post.setTag(postDto.getTagId() != null && postDto.getTagId() != 0 ? Tag.builder().id(postDto.getTagId()).build() : null); // 선택
            post.setImage(postDto.getImageId() != null && postDto.getImageId() != 0 ? Image.builder().id(postDto.getImageId()).build() : null); // 선택
//            post.setImageUrl(imageUrl);

            postRepository.save(post);

            return DataResponseDto.of(null);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucket, originalFilename, file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    /* 이미지 리스트 조회 */
    public DataResponseDto<ImageListDto> getAllImages() {

        List<Image> imageList = imageRepository.findAll();

        // stream 사용
        List<ImageDto> dtoList = imageList
                .stream()
                .map(d -> new ImageDto(d.getId(), d.getImageUrl()))
                .collect(Collectors.toList());

        return DataResponseDto.of(new ImageListDto(dtoList));
    }

    /* 좋아요 추가 */
    public boolean updateLiked(LikeDto likeDto) {
        // 게시글 id로 포스트 조회
        Optional<Post> post = postRepository.findById(likeDto.getId());

        // 게시글 있을 경우 좋아요 수 업데이트
        if (post.isPresent()) {
            int liked = post.get().getLiked();
            post.get().setLiked(liked + likeDto.getCount());

            postRepository.save(post.get());

            return true;
        }

        return false;
    }

    /* 게시글 리스트 조회 */
    public PageResponseDto<PostListDto> getPostList(Long stationId, Integer pageSize, Integer pageNumber, Long tagId) {
        // stationID 예외처리
        if (!stationRepository.existsById(stationId)) {
            throw new GeneralException(ResponseCode.BAD_REQUEST, "존재하지 않는 역 ID입니다.");
        }

        // 페이지 넘버 예외처리
        if(pageNumber <= 0) {
            throw new GeneralException(ResponseCode.BAD_REQUEST, "잘못된 페이지 번호입니다.");
        }

        /* pageable 객체 생성 */
        Pageable pageable = PageRequest.of(pageNumber - 1,
                pageSize, Sort.by("createdDatetime").descending());

        // 현재 시간에서 24시간 전 계산 -> 168시간으로 임시 수정
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -168);
        Date date = calendar.getTime();


        Page<Post> postList;
        if (tagId != null) {
            // stationId & tagId로 포스트 조회
            postList = postRepository.findByStationIdAndTagIdAndCreatedDatetimeAfter(stationId, tagId, pageable, date);

        } else {
            // stationId로 포스트 조회
            postList = postRepository.findByStationIdAndCreatedDatetimeAfter(stationId, pageable, date);
        }

        List<PostInfoDto> results = new ArrayList<>();

        for (Post p : postList) {
            String imageUrl = p.getImage() != null ? p.getImage().getImageUrl() : null;
            Long tag = p.getTag() != null ? p.getTag().getId() : null;
            PostInfoDto postInfoDto = new PostInfoDto(p.getId(), p.getNickname(), p.getCreatedDatetime(),
                    p.getContent(), imageUrl, tag, p.getLiked());
            results.add(postInfoDto);
        }

        PageInfoDto pageInfoDto = new PageInfoDto(pageNumber, pageSize, postList.getTotalElements(), postList.getTotalPages());
        return PageResponseDto.of(new PostListDto(results), pageInfoDto);
    }

}
