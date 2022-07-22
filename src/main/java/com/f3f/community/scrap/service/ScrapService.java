package com.f3f.community.scrap.service;

import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.NotFoundScrapByIdException;
import com.f3f.community.exception.scrapException.DuplicatePostException;
import com.f3f.community.exception.scrapException.DuplicateScrapException;
import com.f3f.community.exception.scrapException.DuplicateScrapNameException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.dto.ScrapDto;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.f3f.community.scrap.dto.ScrapDto.*;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;


    // 스크랩 컬렉션 생성
    @Transactional
    public Long createScrapCollection(SaveRequest saveRequest) throws Exception{
        Scrap newScrap = saveRequest.toEntity();
        if (scrapRepository.existsByUser(newScrap.getUser())) {
            if (scrapRepository.existsByName(newScrap.getName())) {
                scrapRepository.save(newScrap);
                return newScrap.getScrapId();
            } else {
                throw new DuplicateScrapException();
            }
        } else {
            scrapRepository.save(newScrap);
            return newScrap.getScrapId();
        }
    }

    // 해당 유저의 전체 스크랩 컬렉션 가져오기
    @Transactional(readOnly = true)
    public List<Scrap> getScrapListByUserId(UserDto userDto) {
        // 스크랩 리포지토리에서 해당 유저의 스크랩 컬렉션만 꺼내올 수 있게
        List<Scrap> result = new ArrayList<>();
        return result;

    }

    // 스크랩 컬렉션에 해당 게시글 리스트 가져오기
    @Transactional(readOnly = true)
    public List<Post> findAllByCollection(Long scrapId) throws Exception {
        if (scrapRepository.existsById(scrapId)) { // 스크랩 컬렉션이 리포지토리에 존재하는지 아이디 값으로 조회
            Scrap scrap = scrapRepository.findByScrapId(scrapId);// 있으면 스크랩 컬렉션에서 포스트 리스트를 리턴

            return scrap.getPostList();
        }else{
            throw new NotFoundScrapByIdException(); // 없는 아이디 값을 전달 받았을때, 예외를 던진다,
        }

    }

    // 포스트에서 스크랩 저장 눌렀을때, 스크랩에 포스트
    @Transactional
    public void saveCollection(Long scrapId, Post post) throws Exception{

        if (scrapRepository.existsById(scrapId)) {
            Scrap scrap = scrapRepository.findByScrapId(scrapId);
            if (!scrap.getPostList().contains(post)) {
                scrap.getPostList().add(post);
                scrapRepository.save(scrap);
            } else {
                throw new DuplicatePostException();
            }

        } else {
            throw new NotFoundScrapByIdException();
        }

    }



    // 스크랩 컬렉션 이름 변경
    @Transactional
    public void updateCollectionName(Long scrapId, String newName) throws Exception {
        Scrap scrap = scrapRepository.findByScrapId(scrapId);
        boolean existsByName = scrapRepository.existsByName(newName);
        if (!existsByName) {
            scrap.updateScrap(newName);
            scrapRepository.save(scrap);
        }else {
            throw new DuplicateScrapNameException();
        }

    }


    // 스크랩 컬렉션 삭제
    @Transactional
    public void deleteCollection(Long scrapId) {
        Scrap scrap = scrapRepository.findByScrapId(scrapId);
        scrapRepository.delete(scrap);
    }

    // 스크랩 컬렉션 아이템 삭제
    @Transactional
    public void deleteCollectionItem(Long scrapId, Long postId) throws Exception{

        if (postRepository.existsById(postId)) { // 게시글이 존재하는지 확인
            if (scrapRepository.existsById(scrapId)) { // 해당 스크랩 컬렉션이 존재하는지 확인
                Scrap scrap = scrapRepository.findByScrapId(scrapId);
                Post post = postRepository.findPostById(postId);
                scrap.getPostList().remove(post); // remove 메소드로 제거하였는데, 성능 문제는 없는지
                scrapRepository.save(scrap);
            } else { // 스크랩 컬렉션 없으면 터지는 예외
                throw new NotFoundScrapByIdException();
            }
        } else { // 게시글 없으면 터지는 예외
            throw new NotFoundPostByIdException();
        }
    }



}
