package com.openhab.core.internal.event.dto;

import static org.osgi.service.event.EventConstants.EVENT_TOPIC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;



//import org.osgi.framework.Filter;
//import org.osgi.service.event.CloudEvent;
//import org.osgi.service.event.CloudEventProperty;

public class CloudEvent {
	/**
	 * The topic of this event.
	 */
	private final String	topic;
	/**
	 * The properties carried by this event. Keys are strings and values are
	 * objects
	 */
	private final CloudEventProperty	properties;

	/**
	 * Constructs an event.
	 * 
	 * @param topic The topic of the event.
	 * @param properties The event's properties (may be {@code null}). A
	 *        property whose key is not of type {@code String} will be
	 *        ignored.
	 * @throws IllegalArgumentException If topic is not a valid topic name.
	 * @since 1.2
	 */
	public CloudEvent(String topic, Map<String, ? > properties) {
		validateTopicName(topic);
		this.topic = topic;
		// safely publish the event properties
		this.properties = (properties instanceof CloudEventProperty) ? (CloudEventProperty) properties
				: new CloudEventProperty(properties);
	}

	/**
	 * Constructs an event.
	 * 
	 * @param topic The topic of the event.
	 * @param properties The event's properties (may be {@code null}). A
	 *        property whose key is not of type {@code String} will be
	 *        ignored.
	 * @throws IllegalArgumentException If topic is not a valid topic name.
	 */
	public CloudEvent(String topic, Dictionary<String, ? > properties) {
		validateTopicName(topic);
		this.topic = topic;
		// safely publish the event properties
		this.properties = new CloudEventProperty(properties);
	}

	/**
	 * Retrieve the value of an event property. The event topic may be retrieved
	 * with the property name &quot;event.topics&quot;.
	 * 
	 * @param name The name of the property to retrieve.
	 * @return The value of the property, or {@code null} if not found.
	 */
	public final Object getProperty(String name) {
		if (EVENT_TOPIC.equals(name)) {
			return topic;
		}
		return properties.get(name);
	}

	/**
	 * Indicate the presence of an event property. The event topic is present
	 * using the property name &quot;event.topics&quot;.
	 * 
	 * @param name The name of the property.
	 * @return {@code true} if a property with the specified name is in the
	 *         event. This property may have a {@code null} value.
	 *         {@code false} otherwise.
	 * @since 1.3
	 */
	public final boolean containsProperty(String name) {
		if (EVENT_TOPIC.equals(name)) {
			return true;
		}
		return properties.containsKey(name);
	}

	/**
	 * Returns a list of this event's property names. The list will include the
	 * event topic property name &quot;event.topics&quot;.
	 * 
	 * @return A non-empty array with one element per property.
	 */
	public final String[] getPropertyNames() {
		int size = properties.size();
		String[] result = new String[size + 1];
		properties.keySet().toArray(result);
		result[size] = EVENT_TOPIC;
		return result;
	}

	/**
	 * Returns the topic of this event.
	 * 
	 * @return The topic of this event.
	 */
	public final String getTopic() {
		return topic;
	}

	/**
	 * Tests this event's properties against the given filter using a case
	 * sensitive match.
	 * 
	 * @param filter The filter to test.
	 * @return true If this event's properties match the filter, false
	 *         otherwise.
	 */
//	public final boolean matches(Filter filter) {
//		return filter.matchCase(new FilterProperties(topic, properties));
//	}

	/**
	 * Compares this {@code CloudEvent} object to another object.
	 * 
	 * <p>
	 * An event is considered to be <b>equal to</b> another event if the topic
	 * is equal and the properties are equal. The properties are compared using
	 * the {@code java.util.Map.equals()} rules which includes identity
	 * comparison for array values.
	 * 
	 * @param object The {@code CloudEvent} object to be compared.
	 * @return {@code true} if {@code object} is a {@code CloudEvent}
	 *         and is equal to this object; {@code false} otherwise.
	 */
	public boolean equals(Object object) {
		if (object == this) { // quick test
			return true;
		}

		if (!(object instanceof CloudEvent)) {
			return false;
		}

		CloudEvent event = (CloudEvent) object;
		return topic.equals(event.topic) && properties.equals(event.properties);
	}

	/**
	 * Returns a hash code value for this object.
	 * 
	 * @return An integer which is a hash code value for this object.
	 */
	public int hashCode() {
		int h = 31 * 17 + topic.hashCode();
		h = 31 * h + properties.hashCode();
		return h;
	}

	/**
	 * Returns the string representation of this event.
	 * 
	 * @return The string representation of this event.
	 */
	public String toString() {
		return getClass().getName() + " [topic=" + topic + "]";
	}

	/**
	 * Called by the constructor to validate the topic name.
	 * 
	 * @param topic The topic name to validate.
	 * @throws IllegalArgumentException If the topic name is invalid.
	 */
	private static void validateTopicName(String topic) {
		char[] chars = topic.toCharArray();
		int length = chars.length;
		if (length == 0) {
			throw new IllegalArgumentException("empty topic");
		}
		for (int i = 0; i < length; i++) {
			char ch = chars[i];
			if (ch == '/') {
				// Can't start or end with a '/' but anywhere else is okay
				if (i == 0 || (i == length - 1)) {
					throw new IllegalArgumentException("invalid topic: "
							+ topic);
				}
				// Can't have "//" as that implies empty token
				if (chars[i - 1] == '/') {
					throw new IllegalArgumentException("invalid topic: "
							+ topic);
				}
				continue;
			}
			if (('A' <= ch) && (ch <= 'Z')) {
				continue;
			}
			if (('a' <= ch) && (ch <= 'z')) {
				continue;
			}
			if (('0' <= ch) && (ch <= '9')) {
				continue;
			}
			if ((ch == '_') || (ch == '-')) {
				continue;
			}
			throw new IllegalArgumentException("invalid topic: " + topic);
		}
	}

	/**
	 * Dictionary to use for Filter matching.
	 */
	static private final class FilterProperties extends
			Dictionary<String, Object> {
		private final String			topic;
		private final CloudEventProperty	properties;

		FilterProperties(String topic, CloudEventProperty properties) {
			this.topic = topic;
			this.properties = properties;
		}

		public Enumeration<Object> elements() {
			Collection<Object> values = properties.values();
			List<Object> result = new ArrayList<Object>(values.size() + 1);
			result.add(topic);
			result.addAll(values);
			return Collections.enumeration(result);
		}

		public Object get(Object key) {
			if (EVENT_TOPIC.equals(key)) {
				return topic;
			}
			return properties.get(key);
		}

		public boolean isEmpty() {
			return false;
		}

		public Enumeration<String> keys() {
			Collection<String> keys = properties.keySet();
			List<String> result = new ArrayList<String>(keys.size() + 1);
			result.add(EVENT_TOPIC);
			result.addAll(keys);
			return Collections.enumeration(result);
		}

		public Object put(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return properties.size() + 1;
		}
	}
}
