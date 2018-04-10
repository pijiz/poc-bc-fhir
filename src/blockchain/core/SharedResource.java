/**
 * 
 */
package blockchain.core;

import java.util.Date;

import blockchain.utils.StringUtil;

/**
 * Resource to be shared among users
 * 
 * @author pierre-jean.breton
 *
 */
public class SharedResource {

	public String resource; // the resource allowed to be accessed by the
	// recipient

	public String resourceId; // 64 characters max

	public Date beginDate; // permission date beginning

	public Date endDate; // permission expire date

	public int octal; // permission in octal (from 0 to 7)

	// constructor
	public SharedResource(String resource, String resourceId, Date beginDate, Date endDate,
			int octal) {
		super();
		this.resource = resource;
		this.resourceId = resourceId;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.octal = octal;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getOctal() {
		return octal;
	}

	public void setOctal(int octal) {
		this.octal = octal;
	}

	// string to be included in the transactions
	public String toString() {
		return resource
				+ resourceId + StringUtil.getDateString(beginDate)
				+ StringUtil.getDateString(endDate)
				+ String.valueOf(octal);
	}
	
	// resource of the URI in FHIR
	public String getUri() {
		return resource + "/" + resourceId;
	}

}
