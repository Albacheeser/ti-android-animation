package com.billdawson.timodules.animation;

/*
 * Copyright (C) 2013 William Dawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import android.view.View;

import com.billdawson.timodules.animation.utils.AnimationUtils;
import com.billdawson.timodules.animation.views.ViewWrapper;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;

enum PropertyDataType {
	FLOAT, INT, UNKNOWN
}

@Kroll.proxy(creatableInModule = AndroidanimationModule.class)
public class ObjectAnimatorProxy extends AnimatorProxy {
	private static final String TAG = "ObjectAnimatorProxy";
	private static final String PROPERTY_BACKGROUND_COLOR = "backgroundColor";
	private static final String ERR_INT_VALUE = "Values must be set to numeric array or array of strings containing color codes.";

	private PropertyDataType mPropertyType;
	private String mPropertyName;
	private float[] mFloatValues;
	private int[] mIntValues;
	private int mRepeatCount = AndroidanimationModule.NO_INT_VALUE;
	private int mRepeatMode = AndroidanimationModule.NO_INT_VALUE;
	private int mEvaluator = AndroidanimationModule.NO_INT_VALUE;

	public ObjectAnimatorProxy() {
		super();
		this.mPropertyType = PropertyDataType.UNKNOWN;
	}

	protected ObjectAnimatorProxy(Object object, String propertyName,
			PropertyDataType propertyType, Object[] values) {
		this();
		setTarget(object);
		mPropertyName = propertyName;
		mPropertyType = propertyType;
		if (mPropertyType == PropertyDataType.FLOAT) {
			setFloatValues(values);
		} else if (mPropertyType == PropertyDataType.INT) {
			setIntValues(values);
		}
	}

	@Override
	protected void buildAnimator() {
		ObjectAnimator animator = (ObjectAnimator) getAnimator();
		if (animator == null) {
			if (mPropertyType == PropertyDataType.UNKNOWN) {
				Log.w(TAG, "Property data type unknown, cannot animate.");
				return;
			}

			Object target = getTarget();
			String propertyName = AnimationUtils.translatePropertyName(target,
					mPropertyName);

			Object actualObject = target;

			if (target instanceof TiViewProxy) {
				TiUIView intermediateObject = ((TiViewProxy) target).peekView();
				if (intermediateObject != null) {
					actualObject = intermediateObject.getNativeView();
					if (actualObject != null
							&& propertyName.equals(PROPERTY_BACKGROUND_COLOR)
							&& mIntValues.length == 1) {
						// There is no "getBackgroundColor" on Android views,
						// so we wrap it.
						actualObject = new ViewWrapper((View) actualObject);
					}
				} else {
					Log.w(TAG, "View not available for animation.");
				}
			}

			if (actualObject == null) {
				Log.w(TAG, "Object not available for animation (null).");
				return;
			}

			switch (mPropertyType) {
			case FLOAT:
				animator = ObjectAnimator.ofFloat(actualObject, propertyName,
						mFloatValues);
				break;
			case INT:
				animator = ObjectAnimator.ofInt(actualObject, propertyName,
						mIntValues);
				break;
			case UNKNOWN:
				break;
			}

		}

		if (mRepeatCount != AndroidanimationModule.NO_INT_VALUE) {
			animator.setRepeatCount(mRepeatCount);
		}

		if (mRepeatMode != AndroidanimationModule.NO_INT_VALUE) {
			animator.setRepeatMode(mRepeatMode);
		}

		if (mEvaluator != AndroidanimationModule.NO_INT_VALUE) {
			switch (mEvaluator) {
			case AndroidanimationModule.INT_EVALUATOR:
				animator.setEvaluator(new IntEvaluator());
				break;
			case AndroidanimationModule.FLOAT_EVALUATOR:
				animator.setEvaluator(new FloatEvaluator());
				break;
			case AndroidanimationModule.ARGB_EVALUATOR:
				animator.setEvaluator(new ArgbEvaluator());
				break;
			default:
				Log.w(TAG, "Evaluator set to unknown value: " + mEvaluator);
			}
		}

		setAnimator(animator);

		super.setCommonAnimatorProperties();

	}

	// Public-facing Kroll methods/properties.

	@Kroll.method
	@Kroll.getProperty
	public int getEvaluator() {
		return mEvaluator;
	}

	@Kroll.method
	@Kroll.setProperty
	public void setEvaluator(int evaluator) {
		mEvaluator = evaluator;
	}

	@Kroll.method
	public void reverse() {
		buildAnimator();
		final ObjectAnimator animator = (ObjectAnimator) getAnimator();
		if (animator != null) {
			TiMessenger.postOnMain(new Runnable() {
				@Override
				public void run() {
					animator.reverse();
				}
			});
		} else {
			Log.w(TAG, WARN_ANIMATOR);
		}
	}

	@Kroll.method
	@Kroll.getProperty
	public String getPropertyName() {
		return mPropertyName;
	}

	@Kroll.method
	@Kroll.setProperty
	public void setPropertyName(String propertyName) {
		this.mPropertyName = propertyName;
	}

	@Kroll.method
	@Kroll.setProperty
	public void setIntValues(Object[] values) {
		if (mPropertyType == PropertyDataType.UNKNOWN) {
			mPropertyType = PropertyDataType.INT;
		}

		if (values == null || values.length == 0) {
			mIntValues = null;
			return;
		}

		mIntValues = new int[values.length];

		for (int i = 0; i < values.length; i++) {
			Object member = values[i];
			if (member instanceof Number) {
				mIntValues[i] = ((Number) member).intValue();
			} else if (member instanceof String) {
				try {
					int colorVal = TiConvert.toColor((String) member);
					mIntValues[i] = colorVal;
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(ERR_INT_VALUE);
				}
			} else {
				throw new IllegalArgumentException(ERR_INT_VALUE);
			}
		}
	}

	@Kroll.method
	@Kroll.setProperty
	public void setFloatValues(Object[] values) {
		if (mPropertyType == PropertyDataType.UNKNOWN) {
			mPropertyType = PropertyDataType.FLOAT;
		}

		if (values == null || values.length == 0) {
			mFloatValues = null;
			return;
		}

		mFloatValues = AnimationUtils.unboxFloatValues(values);

	}

	@Kroll.method
	@Kroll.getProperty
	public int getRepeatCount() {
		return mRepeatCount;
	}

	@Kroll.method
	@Kroll.setProperty
	public void setRepeatCount(int repeatCount) {
		this.mRepeatCount = repeatCount;
	}

	@Kroll.method
	@Kroll.getProperty
	public int getRepeatMode() {
		return mRepeatMode;
	}

	@Kroll.method
	@Kroll.setProperty
	public void setRepeatMode(int repeatMode) {
		this.mRepeatMode = repeatMode;
	}

}
