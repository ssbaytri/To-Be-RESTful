package fr._42.educationcenter.hateoas;

import fr._42.educationcenter.models.Course;
import fr._42.educationcenter.models.CourseState;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class CoursePublishLinkProcessor implements RepresentationModelProcessor<EntityModel<Course>> {

    @Override
    public EntityModel<Course> process(EntityModel<Course> model) {
        Course course = model.getContent();
        if (course != null && course.getState() == CourseState.DRAFT) {
            model.add(Link.of("/courses/" + course.getId() + "/publish", "publish"));
        }
        return model;
    }
}