package com.codingwithmitch.openapi.ui.main.blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.onBlogPostUpdateSuccess
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.setUpdatedBlogFields
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogStateEvent
import com.codingwithmitch.openapi.util.Constants
import com.codingwithmitch.openapi.util.ErrorHandling
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import kotlinx.android.synthetic.main.fragment_update_blog.*
import kotlinx.android.synthetic.main.fragment_update_blog.blog_body
import kotlinx.android.synthetic.main.fragment_update_blog.blog_image
import kotlinx.android.synthetic.main.fragment_update_blog.blog_title
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UpdateBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        subscribeObservers()
        image_container.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                /** should check null due to process death **/
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.getContentIfNotHandled()?.let { viewState ->
                        //if this is not null, blogpost was updated...
                        viewState.viewBlogFields.blogPost?.let { blogPost ->
                            viewModel.onBlogPostUpdateSuccess(blogPost).let {
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.updatedBlogFields.let { updatedBlogFields ->
                setBlogProperties(
                    updatedBlogFields.updatedBlogTitle,
                    updatedBlogFields.updatedBlogBody,
                    updatedBlogFields.updatedImageUri
                )

            }
        })
    }

    private fun setBlogProperties(
        updatedBlogTitle: String?,
        updatedBlogBody: String?,
        updatedImageUri: Uri?
    ) {
        dependencyProvider.getGlideRequestManager().load(updatedImageUri).into(blog_image)
        blog_title.setText(updatedBlogTitle)
        blog_body.setText(updatedBlogBody)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image.png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    } ?: showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri : $resultUri")
                    viewModel.setUpdatedBlogFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri?) {
        context?.let {
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).start(it, this)
        }
    }

    private fun saveChanges() {
        var multipartBody: MultipartBody.Part? = null
        viewModel.viewState.value?.updatedBlogFields?.updatedImageUri?.let { imageUri ->
            imageUri.path?.let { filePath ->
                val imageFile = File(filePath)
                Log.d(TAG, "UpdatedBlogFragment : imageFile : ${imageFile}")
                val requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    imageFile
                )
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
            multipartBody?.let {
                viewModel.setStateEvent(
                    BlogStateEvent.UpdatedBlogPostEvent(
                        blog_title.text.toString(),
                        blog_body.text.toString(),
                        it
                    )
                )
                stateChangeListener.hideSoftKeyboard()
            } ?: showErrorDialog(ErrorHandling.ERROR_MUST_SELECT_IMAGE)
        }
    }

    override fun onPause() { //saving for orientation
        super.onPause()
        viewModel.setUpdatedBlogFields(
            uri = null,
            title = blog_title.text.toString(),
            body = blog_body.text.toString()
        )
    }


    private fun showErrorDialog(errorMessage: String) {
        stateChangeListener.onDataStateChange(
            DataState(
                Event(
                    StateError(
                        Response(
                            errorMessage, ResponseType.Dialog()
                        )
                    )
                ),
                Loading(false),
                Data(Event.dataEvent(null), null)
            )
        )
    }
}