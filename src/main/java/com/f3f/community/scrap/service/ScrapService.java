package com.f3f.community.scrap.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;

    // 해당 유저의 전체 스크랩 컬렉션 가져오기
    public List<Scrap> findAll(User user) {
        // 스크랩 리포지토리에서 해당 유저의 스크랩 컬렉션만 꺼내올 수 있게
        return scrapRepository.findByUser(user);
    }

    // 스크랩 컬렉션에 해당 게시글 리스트 가져오기
    public List<Post> findAllByCollection(Long scrapId) throws Exception {
        if (scrapRepository.existsById(scrapId)) { // 스크랩 컬렉션이 리포지토리에 존재하는지 아이디 값으로 조회
            Scrap scrap = scrapRepository.findByIdScrap(scrapId);// 있으면 스크랩 컬렉션에서 포스트 리스트를 리턴
            List<Post> returnPost = new ArrayList<>(); // 리턴할 포스트 리스트
            for (Post post : scrap.getPostList()) {
                // 스크랩에 포스트 리스트에 포스트가 존재하는지를 알기 위해서 반복문으로 하나하나 돌면서 확인하였습니다
                // 만약 post가 null이면, 아래 getId를 수행할때 예외가 터지게 하였고
                // 리포지토리에도 존재하면, 리턴할 리스트에 넣어주게하였습니당
                if (postRepository.existsById(post.getId())) {
                    returnPost.add(post);
                }
            }
            return returnPost;
        }else{
            throw new IllegalArgumentException(); // 없는 아이디 값을 전달 받았을때, 예외를 던진다,
        }


    }

    // 포스트에서 스크랩 저장 눌렀을때, 스크랩에 포스트
    public void saveCollection(Long scrapId, Post post) throws Exception{

        if (scrapRepository.existsById(scrapId)) {
            Scrap scrap = scrapRepository.findByIdScrap(scrapId);
            scrap.getPostList().add(post);
            scrapRepository.save(scrap);
        } else {
            throw new IllegalStateException();
        }

    }
    // 스크랩에 포스트를 저장하는 메소드를 두 방식으로 만들어보았습니당
    // 아직까지는 어떤 상황에서 NPE가 터지는지 모르겠습니당
    // 언제 NPE가 터질지 모르니까 아래 코드처럼 옵셔널을 반환하는게 좋은가요 아니면
    // 예외를 발생하는게 좀 더 나을까요??
    public void saveCollection(String scrapName, Post post) {
        Optional<Scrap> scrap = scrapRepository.findByName(scrapName);
        if (scrap.isPresent()) {
            scrap.get().getPostList().add(post);
            scrapRepository.save(scrap.get());
        }
    }


    // 스크랩 컬렉션 이름 변경
    public void updateCollectionName(Long scrapId, String newName) throws Exception {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        boolean existsById = scrapRepository.existsById(scrapId);
        if (!existsById) {
            if (scrap.getName().equals(newName)) {
                throw new IllegalStateException();
            } else {
                scrap.updateScrap(newName);
            }
        }else {
            throw new IllegalArgumentException();
        }
        scrapRepository.save(scrap);
    }


    // 스크랩 컬렉션 삭제
    public void deleteCollection(Long scrapId) {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        scrapRepository.delete(scrap);
    }

    // 스크랩 컬렉션 아이템 삭제
    public void deleteCollectionItem(Long scrapId, Long postId) throws Exception{

        if (postRepository.existsById(postId)) { // 게시글이 존재하는지 확인
            if (scrapRepository.existsById(scrapId)) { // 해당 스크랩 컬렉션이 존재하는지 확인
                Scrap scrap = scrapRepository.findByIdScrap(scrapId);
                Post post = postRepository.findByIdPost(postId);
                scrap.getPostList().remove(post); // remove 메소드로 제거하였는데, 성능 문제는 없는지
                scrapRepository.save(scrap);
            } else { // 스크랩 컬렉션 없으면 터지는 예외
                throw new IllegalArgumentException();
            }
        } else { // 게시글 없으면 터지는 예외
            throw new IllegalArgumentException();
        }


    }

}
