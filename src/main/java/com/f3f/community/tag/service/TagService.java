package com.f3f.community.tag.service;

import com.f3f.community.common.constants.ResponseConstants;
import com.f3f.community.exception.common.DuplicateException;
import com.f3f.community.exception.common.NotFoundByIdException;
import com.f3f.community.exception.tagException.DuplicateTagNameException;
import com.f3f.community.exception.tagException.NotFoundPostTagException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.tag.domain.PostTag;
import com.f3f.community.tag.domain.Tag;
import com.f3f.community.tag.dto.TagDto;
import com.f3f.community.tag.repository.PostTagRepository;
import com.f3f.community.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.f3f.community.common.constants.ResponseConstants.DELETE;

@Service
@RequiredArgsConstructor
public class TagService {
    /*
    1. C
        - find로
    2. R
    3. U
    4. D
     */

    private final TagRepository tagRepository;

    private final PostRepository postRepository;

    private final PostTagRepository postTagRepository;
    @Transactional
    public Long createTag(TagDto.SaveRequest saveRequest) {
        if (tagRepository.existsByTagName(saveRequest.getTagName())) {
            throw new DuplicateTagNameException();
        }

        Tag tag = saveRequest.toEntity();
        tagRepository.save(tag);
        return tag.getId();
    }


    @Transactional
    public Long addTagToPost(Long tagId, Long postId) throws Exception {

        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 태그가 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 포스트가 없습니다"));

        if (postTagRepository.existsByPostAndTag(post, tag)) {
            throw new DuplicateException("해당 태그가 이미 포스트에 존재합니다");
        } else {
            PostTag postTag = PostTag.builder().post(post).tag(tag).build();
            postTagRepository.save(postTag);
            tag.getPostTags().add(postTag);
            post.getTagList().add(postTag);
            return postTag.getId();
        }
    }

    @Transactional
    public String deleteTagFromPost(Long tagId, Long postId) throws Exception{
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 스크랩이 없습니다"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 포스트가 없습니다"));


        PostTag postTag = postTagRepository.findByPostAndTag(post, tag).orElseThrow(NotFoundPostTagException::new);

//        tag.getPostTags().remove(postTag);
//        post.getTagList().remove(postTag);
        postTagRepository.delete(postTag);
        return DELETE;

    }


    @Transactional
    public String deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 태그가 없습니다"));
        tagRepository.delete(tag);

        return DELETE;
    }

    @Transactional
    public List<Post> getPosts(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 태그가 없습니다"));
        List<Post> posts = new ArrayList<>();
        List<PostTag> postTagsByTagId = postTagRepository.findPostTagsByTagId(tagId);
        for (PostTag postTag : postTagsByTagId) {
            posts.add(postTag.getPost());
        }
        return posts;
    }

}
