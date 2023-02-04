package fr.hyriode.hyggdrasil.api.event.model;

import fr.hyriode.hyggdrasil.api.event.HyggEvent;

/**
 * Created by AstFaster
 * on 04/02/2023 at 09:26.<br>
 *
 * Event triggered each time a template is updated.<br>
 * An update is done each time a file of the template is re-downloaded.
 */
public class HyggTemplateUpdatedEvent extends HyggEvent {

    /** The updated template. E.g. lobby */
    private final String template;

    /**
     * Constructor of a {@link HyggTemplateUpdatedEvent}
     *
     * @param template The name of the template
     */
    public HyggTemplateUpdatedEvent(String template) {
        this.template = template;
    }

    /**
     * Get the template that has been updated
     *
     * @return A template name
     */
    public String getTemplate() {
        return this.template;
    }

}
