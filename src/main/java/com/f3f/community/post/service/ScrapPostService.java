package com.f3f.community.post.service;

import com.f3f.community.exception.scrapException.NotFoundScrapByIdException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapPostService {
    private final ScrapPostRepository scrapPostRepository;
    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<Post> getPostsOfScrap(Long sid) throws Exception{
        Scrap scrap = scrapRepository.findById(sid).orElseThrow(NotFoundScrapByIdException::new);
        List<ScrapPost> scrapPosts = scrapPostRepository.findScrapPostsByScrap(scrap);
        List<Post> result = new ArrayList<>();
        for (ScrapPost scrapPost : scrapPosts) {
            result.add(postRepository.findById(scrapPost.getPost().getId()).get());
        }
        return result;
    }
}
