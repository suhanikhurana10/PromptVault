package com.promptvault.entity;

import com.promptvault.enums.PromptStatus;
import com.promptvault.enums.Visibility;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(
        name = "prompts",
        indexes = {
                @Index(name = "idx_prompts_owner", columnList = "owner_id"),
                @Index(name = "idx_prompts_collection", columnList = "collection_id"),
                @Index(name = "idx_prompts_status", columnList = "status"),
                @Index(name = "idx_prompts_favorite", columnList = "favorite"),
                @Index(name = "idx_prompts_visibility", columnList = "visibility"),
                @Index(name = "idx_prompts_title", columnList = "title")
        }
)
//for every field we hace getter adn setter
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor //when all the field are provided by user in the same order as in the db
@Builder //difficult to maintain allargs bcs need to rememeber every field so builder helps to only use specific field like .title.content etc
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, exclude = {"owner", "collection", "tags", "versions"})
public class Prompt extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank
    @Lob //for long texts
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PromptStatus status = PromptStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;

    @Column(name = "favorite", nullable = false)
    @Builder.Default
    private boolean favorite = false;

    @Column(name = "is_template", nullable = false)
    //what default value should be there if none is given by the user
    @Builder.Default
    private boolean template = false;

    @Column(name = "current_version", nullable = false)
    @Builder.Default
    private int currentVersion = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //not neccesarily be there since it does not have not nullable field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    //auto geenrated table for many to many relationship between prompts and tags
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "prompt_tags",
            joinColumns = @JoinColumn(name = "prompt_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_prompt_tags_prompt", columnList = "prompt_id"),
                    @Index(name = "idx_prompt_tags_tag", columnList = "tag_id")
            }
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
 //prompt version
    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) //cascade all meanning w=if the prompt is deleted all the versions hould be deletd too
    @OrderBy("versionNumber DESC")
    @Builder.Default
    private Set<PromptVersion> versions = new HashSet<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }
}
